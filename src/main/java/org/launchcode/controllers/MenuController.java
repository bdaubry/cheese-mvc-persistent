package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("menus", menuDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors, Model model) {


        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:/menu/view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());
        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("title", "Add item to: " + menu.getName());
        model.addAttribute("form", addMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm, Errors errors,@RequestParam int menuId, @RequestParam int cheeseId) {
        if (errors.hasErrors()) {
            return "menu/add-item";
        }

        //Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
        //Cheese cheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu menu = menuDao.findOne(menuId);
        Cheese cheese = cheeseDao.findOne(cheeseId);
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:/menu/view/" + menu.getId();
    }

}
