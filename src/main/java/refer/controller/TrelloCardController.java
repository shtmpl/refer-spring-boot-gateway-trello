package refer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import refer.controller.request.RequestCardIndex;
import refer.gateway.trello.TrelloGateway;
import refer.gateway.trello.response.ResponseTrelloCard;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/trello/card")
@RestController
public class TrelloCardController {

    private final TrelloGateway trelloGateway;

    @Autowired
    public TrelloCardController(TrelloGateway trelloGateway) {
        this.trelloGateway = trelloGateway;
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseTrelloCard>> index(@Valid RequestCardIndex request) {
        String idList = request.getIdList();
        if (idList != null) {
            return ResponseEntity.ok(trelloGateway.withAuth((TrelloGateway.Auth auth) ->
                    trelloGateway.indexListCards(auth, idList)));
        }

        String idBoard = request.getIdBoard();
        return ResponseEntity.ok(trelloGateway.withAuth((TrelloGateway.Auth auth) ->
                trelloGateway.indexBoardCards(auth, idBoard)));
    }
}
