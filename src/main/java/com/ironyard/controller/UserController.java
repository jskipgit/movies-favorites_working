package com.ironyard.controller;

import com.ironyard.data.Movie;
import com.ironyard.data.MovieUser;
import com.ironyard.repo.MovieRepo;
import com.ironyard.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jasonskipper on 2/6/17.
 */
@Controller
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MovieRepo movieRepo;

    @RequestMapping(path = "/secure/user/favorites/remove", method = RequestMethod.GET)
    public String removeMovieFromFavorites(Model data, HttpSession session, @RequestParam Long id){
        MovieUser theUser = (MovieUser) session.getAttribute("user");
        theUser = userRepo.findOne(theUser.getId());
        Movie aMovieToRm = movieRepo.findOne(id);
        theUser.getFavorites().remove(aMovieToRm);
        userRepo.save(theUser);
        // redirect since Im not using succes message, they will see it's gone
        return "redirect:/secure/user/favorites/list";
    }

    @RequestMapping(path = "/secure/user/favorites/add", method = RequestMethod.GET)
    public String addMovieToFavorites(Model data, HttpSession session, @RequestParam Long id){
        MovieUser theUser = (MovieUser) session.getAttribute("user");
        theUser = userRepo.findOne(theUser.getId());
        Movie aMovieToAdd = movieRepo.findOne(id);
        // only add if user doesnt already have it
        if(!theUser.getFavorites().contains(aMovieToAdd)) {
            theUser.getFavorites().add(aMovieToAdd);
            userRepo.save(theUser);
            data.addAttribute("success_msg","Favorite added!");
        }else{
            data.addAttribute("success_msg","Favorite already added!");
        }

        return "forward:/secure/movies";
    }

    @RequestMapping(path = "/secure/user/favorites/list", method = RequestMethod.GET)
    public String listFavoriteMovies(HttpSession session, Model data){
        MovieUser found = (MovieUser) session.getAttribute("user");
        // re fetch from DB so we have the latest
        found = userRepo.findOne(found.getId());
        data.addAttribute("favoriteMovies", found.getFavorites());
        return "/secure/favorites";
    }

    @RequestMapping(path = "/open/authenticate", method = RequestMethod.POST)
    public String login(HttpSession session, Model data, @RequestParam(name = "username") String usr, @RequestParam String password){
        MovieUser found = userRepo.findByUsernameAndPassword(usr, password);
        String destinationView = "home";
        if(found == null){
            // no user found, login must fail
            destinationView = "login";
            data.addAttribute("message", "User/Pass combination not found.");
        }else{
            session.setAttribute ("user", found);
            destinationView = "redirect:/secure/movies";
        }
        return destinationView;
    }
}
