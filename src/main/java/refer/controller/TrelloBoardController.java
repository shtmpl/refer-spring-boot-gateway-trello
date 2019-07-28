package refer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import refer.controller.request.RequestCardIndex;
import refer.gateway.trello.TrelloGateway;
import refer.gateway.trello.response.ResponseTrelloBoard;
import refer.gateway.trello.response.ResponseTrelloCard;
import refer.gateway.trello.response.ResponseTrelloList;

import java.util.List;

@RequestMapping("/trello/board")
@RestController
public class TrelloBoardController {

    private final TrelloGateway trelloGateway;

    private final TrelloListController trelloListController;
    private final TrelloCardController trelloCardController;

    @Autowired
    public TrelloBoardController(TrelloGateway trelloGateway,
                                 TrelloListController trelloListController,
                                 TrelloCardController trelloCardController) {
        this.trelloGateway = trelloGateway;

        this.trelloListController = trelloListController;
        this.trelloCardController = trelloCardController;
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseTrelloBoard>> index() {
        List<ResponseTrelloBoard> result = trelloGateway.withAuth(trelloGateway::indexBoards);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{idBoard}")
    public ResponseEntity<ResponseTrelloBoard> show(@PathVariable String idBoard) {
        ResponseTrelloBoard result = trelloGateway.withAuth((TrelloGateway.Auth auth) ->
                trelloGateway.showBoard(auth, idBoard));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{idBoard}/list")
    public ResponseEntity<List<ResponseTrelloList>> indexLists(@PathVariable String idBoard) {
        return trelloListController.index(idBoard);
    }

    @GetMapping("/{idBoard}/card")
    public ResponseEntity<List<ResponseTrelloCard>> indexCards(@PathVariable String idBoard) {
        RequestCardIndex request = new RequestCardIndex();
        request.setIdBoard(idBoard);

        return trelloCardController.index(request);
    }
}
