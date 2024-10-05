package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class AdminPasswordController {
    private AdminPasswordService adminPasswordService;

    /**
     * Constructor
     * @param adminPasswordService service for the admin password
     */
    @Autowired
    public AdminPasswordController(AdminPasswordService adminPasswordService) {
        this.adminPasswordService = adminPasswordService;
    }

    /**
     * check if the user has the correct password
     * @param inputPassword the password we want to check
     * @return a boolean, true or false, if the password is correct or not
     */
    @GetMapping("/{inputPassword}")
    ResponseEntity<Boolean> checkPassword(@PathVariable String inputPassword){
        if(adminPasswordService.passwordChecker(inputPassword)){
            return ResponseEntity.ok(true);
        }
        else{
            return ResponseEntity.ok(false);
        }
    }
}
