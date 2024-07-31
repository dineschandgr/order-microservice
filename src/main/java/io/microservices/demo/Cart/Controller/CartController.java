package io.microservices.demo.Cart.Controller;

import io.microservices.demo.Cart.Service.CartService;
import io.microservices.demo.Cart.model.Cart;
import io.microservices.demo.Cart.model.CartDTO;
import io.microservices.demo.Cart.model.CartItem;
import io.microservices.demo.Configuration.UserContext;
import io.microservices.demo.Integration.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    @PostMapping
    public String addToCart(@RequestBody CartDTO cartDTO) throws Exception {
        LOGGER.info("inside addToCart {} ",cartDTO);
        try {
            cartService.addToCart(cartDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "User does not exist", e);
        }

        return "success";
    }

    @GetMapping("/{id}")
    public Cart getCart(@PathVariable Integer id) throws Exception {
        return cartService.getCartById(id);
    }

    @PutMapping("/{id}")
    public Cart updateCart(@PathVariable Integer id, @RequestBody CartItem cartItem) throws Exception {
        return cartService.updateCart(id, cartItem);
    }

    @DeleteMapping("/item/{cartItemId}")
    public void deleteCartItem(@PathVariable Integer cartItemId) throws Exception {
         cartService.deleteCartItem(cartItemId);
    }

   /* @DeleteMapping("/{id}")

    public ResponseEntity<String> removeItemFromCart(@PathVariable Integer id) throws Exception {
        cartService.removeItemFromCart(id);
        return ResponseEntity.ok("delete success");

    }*/
    @PostMapping("/checkout")
    public String createOrder() throws Exception {
        Long userId = UserContext.getUserId();
        cartService.createOrder(userId);
        return "success";


    }


}
