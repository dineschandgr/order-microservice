package io.microservices.demo.Cart.Service;

import io.microservices.demo.Cart.Repository.CartItemRepository;
import io.microservices.demo.Cart.Repository.CartRepository;
import io.microservices.demo.Cart.model.Cart;
import io.microservices.demo.Cart.model.CartDTO;
import io.microservices.demo.Cart.model.CartItem;
import io.microservices.demo.Configuration.UserContext;
import io.microservices.demo.Integration.model.Product;
import io.microservices.demo.Integration.model.User;
import io.microservices.demo.Integration.service.CommonService;
import io.microservices.demo.Order.Service.OrderService;
import io.microservices.demo.Order.model.Order;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CommonService commonService;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private OrderService orderService;

    private final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    @Transactional
    public void addToCart(CartDTO cartDTO) throws Exception {

        Long userId = UserContext.getUserId();

        LOGGER.info(" inside addToCart {} ",userId);

        User user = commonService.findUserById(userId);

        if(user == null)
            throw new Exception("User Not Found");

        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());

        cart.setUserId(userId);

        cart = cartRepository.save(cart);

        Product product = commonService.findProductById(cartDTO.getCartItem().getTempProductId());

        //CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(new CartItem());

        int quantity = cartDTO.getCartItem().getQuantity();
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProductId(product.getId());
        cartItem.setQuantity(quantity);
        cartItem.setProductPrice(product.getPrice());
        calculateTotal(cartItem, cart, product);

        cartRepository.save(cart);

    }

    public Cart calculateTotal(CartItem cartItem, Cart cart, Product product) {

        double itemTotal = cartItem.getQuantity() * product.getPrice();
        double gstAmount = itemTotal * (product.getGstPercentage() / 100);
        cartItem.setItemTotal(itemTotal);
        cartItem.setGstAmount(gstAmount);

        cartItemRepository.save(cartItem);

        List<CartItem> cartItemList = cartItemRepository.findByCartId(cart.getId());

        double finalTotal = cartItemList.stream().mapToDouble(item -> item.getItemTotal()).sum();
        double gstTotal = cartItemList.stream().mapToDouble(item -> item.getGstAmount()).sum();
        double finalTotalWithGst = finalTotal + gstTotal;

        cart.setTotalPrice(finalTotal);
        cart.setGstAmount(gstTotal);
        cart.setTotalAmountWithGST(finalTotalWithGst);

        return cart;
    }

    public Cart getCartById(Integer id) throws Exception {
        Optional<Cart> cart = cartRepository.findById(id);
        return cart.orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public Cart updateCart(Integer cartId, CartItem cartItem) throws Exception {
        Cart cart = getCartById(cartId);

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),
                cartItem.getTempProductId()).orElseThrow(() -> new Exception("cart item not found"));

        Product product = commonService.findProductById(cartItem.getTempProductId());

        existingItem.setQuantity(cartItem.getQuantity());
        existingItem.setProductPrice(product.getPrice());

        calculateTotal(existingItem, cart, product);

        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteCartItem(Integer cartItemId) throws Exception {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow( () -> new Exception("Cart Item Not Found"));

        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow( () -> new Exception("Cart Not Found"));

        cartItemRepository.deleteOrderItemById(cartItemId);

        System.out.println("Cart item deleted successfully");

        List<CartItem> existingItems = cartItemRepository.findByCartId(cart.getId());

        double finalTotal = existingItems.stream().mapToDouble(item -> item.getItemTotal()).sum();
        double gstTotal = existingItems.stream().mapToDouble(item -> item.getGstAmount()).sum();
        double finalTotalWithGst = finalTotal + gstTotal;

        cart.setTotalPrice(finalTotal);
        cart.setGstAmount(gstTotal);
        cart.setTotalAmountWithGST(finalTotalWithGst);
        cartRepository.save(cart);

    }

    @Transactional
    public void createOrder(Long userId) throws Exception {

        //   Find the cart by user ID
         Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new Exception("User does not exists"));

        //  Create the order

         Order order = orderService.createOrder(cart);

         cart.setOrderId(order.getId());

         cartRepository.save(cart);

         System.out.println("order completed successfully");



        // cartRepository.delete(cart);



        // Step 1 - Create Order

        //Step 2 - Create payment(order)

        //Step 3 - Update order to completed

        //Step 4 - Delete Cart Items


    }

    public void deleteCart(Long userId) throws Exception {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow( () -> new Exception("Cart Not Found"));
        cartRepository.delete(cart);

    }
}












