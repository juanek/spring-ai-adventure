package ar.com.accn.adventure.controller;



import ar.com.accn.adventure.model.AdventureDecisionRequest;
import ar.com.accn.adventure.model.AdventureDecisionResponse;
import ar.com.accn.adventure.model.AdventureRequest;
import ar.com.accn.adventure.model.AdventureResponse;
import ar.com.accn.adventure.service.AdventureService;
import ar.com.accn.adventure.service.DecisionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adventure")
public class AdventureController {

    private final AdventureService adventureService;
    private final DecisionService decisionService;

    public AdventureController(AdventureService adventureService, DecisionService decisionService) {
        this.adventureService = adventureService;
        this.decisionService = decisionService;
    }

    @PostMapping("/generate")
    public AdventureResponse generate(@RequestBody AdventureRequest request) {
        return adventureService.generateAdventure(request);
    }

    @PostMapping("/decision")
    public AdventureDecisionResponse decision(@RequestBody AdventureDecisionRequest request) {
        return decisionService.continueAdventure(request);
    }
}

