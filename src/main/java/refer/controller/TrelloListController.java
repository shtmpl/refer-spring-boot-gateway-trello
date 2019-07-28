package refer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import refer.controller.request.RequestCardIndex;
import refer.gateway.trello.TrelloGateway;
import refer.gateway.trello.response.ResponseTrelloCard;
import refer.gateway.trello.response.ResponseTrelloList;

import java.util.List;

@RequestMapping("/trello/list")
@RestController
public class TrelloListController {

    private final TrelloGateway trelloGateway;

    private final TrelloCardController trelloCardController;

    @Autowired
    public TrelloListController(TrelloGateway trelloGateway,
                                TrelloCardController trelloCardController) {
        this.trelloGateway = trelloGateway;

        this.trelloCardController = trelloCardController;
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseTrelloList>> index(@RequestParam String idBoard) {
        List<ResponseTrelloList> result = trelloGateway.withAuth((TrelloGateway.Auth auth) ->
                trelloGateway.indexBoardLists(auth, idBoard));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{idList}/card")
    public ResponseEntity<List<ResponseTrelloCard>> indexCards(@PathVariable String idList) {
        RequestCardIndex request = new RequestCardIndex();
        request.setIdList(idList);

        return trelloCardController.index(request);
    }
}
