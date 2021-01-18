package com.heatingManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RoomsController {

    @Autowired
    RoomRepository repo;

    @GetMapping("/home/rooms")
    public String roomsManagementPage(Model model) {
        //List<Room> rooms = (List<Room>) repo.findAll();


        model.addAttribute("rooms", repo.findAll());
        model.addAttribute("room", new Room());
        return "rooms";
    }

    @PostMapping("/home/rooms")
    public String createRoom(Room room, Model model) {
        repo.save(room);
        return "redirect:/home/rooms";
    }

}