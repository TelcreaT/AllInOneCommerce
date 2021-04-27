package com.telcreat.aio.viewController;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.telcreat.aio.model.*;
import com.telcreat.aio.service.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Data
@RequestScope
@Controller
public class viewController {

    private final CartService cartService;
    private final ItemService itemService;
    private final PictureService pictureService;
    private final ShopOrderService shopOrderService;
    private final UserService userService;
    private final VariantService variantService;
    private final CategoryService categoryService;
    private final VerificationTokenService verificationTokenService;
    private final FileUploaderService fileUploaderService;
    private final ShopService shopService;
    private final HttpServletRequest request;

    private User loggedUser;
    private boolean isLogged = false;
    private User.UserRole loggedRole = User.UserRole.CLIENT;
    private int loggedId;
    private boolean isOwner;

    @Autowired
    public viewController(CartService cartService, ItemService itemService, PictureService pictureService, ShopOrderService shopOrderService, UserService userService, VariantService variantService, CategoryService categoryService, VerificationTokenService verificationTokenService, FileUploaderService fileUploaderService, ShopService shopService, HttpServletRequest request) {
        this.cartService = cartService;
        this.itemService = itemService;
        this.pictureService = pictureService;
        this.shopOrderService = shopOrderService;
        this.userService = userService;
        this.variantService = variantService;
        this.categoryService = categoryService;
        this.verificationTokenService = verificationTokenService;
        this.fileUploaderService = fileUploaderService;
        this.shopService = shopService;
        this.request = request;

        loggedUser = userService.getLoggedUser();
        if (loggedUser != null){
            isLogged = true;
            loggedId = loggedUser.getId();
            loggedRole = loggedUser.getUserRole();
            if (loggedRole == User.UserRole.OWNER){
                isOwner = true;
            }
        }
    }


    // Search View
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String searchView(@RequestParam(name = "categoryId", required = false, defaultValue = "0") Integer categoryId,
                             @RequestParam(name = "orderCriteriaId", required = false, defaultValue = "0") Integer orderCriteriaId,
                             @RequestParam(name = "search", required = false, defaultValue = "") String itemName,
                             ModelMap modelMap) throws IOException, GeoIp2Exception {

        // DEFAULT INFORMATION IN ALL VIEWS
        modelMap.addAttribute("isLogged", isLogged);
        modelMap.addAttribute("loggedUserId", loggedId);
        modelMap.addAttribute("loggedUserRole", loggedRole);
        modelMap.addAttribute("isOwner", isOwner);
        Shop shop = shopService.findActiveShopByOwnerId(loggedId);
        if (shop != null){
            modelMap.addAttribute("loggedShopId",shop.getId());
        }

        // Get remote IP debug
        // modelMap.addAttribute("clientIP", request.getRemoteAddr());
        // FIND CLIENTS IP ADDRESS

        // Item Search - Item List based on Category and Name search
        modelMap.addAttribute("itemSearch", itemService.findItemsContainsNameOrdered(itemName, orderCriteriaId, categoryId, "1.1.1.1"));
        modelMap.addAttribute("categories", categoryService.findAllCategories()); // Category List for ItemSearch

        return "index"; // Return Search search.html view
    }




    // CheckOut View
    // Comment: it's not necessary to obtain any cart Id

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String viewCheckout(@RequestParam() ModelMap modelMap){
        return "checkout";
    }

}
