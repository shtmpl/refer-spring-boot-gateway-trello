package refer.gateway.trello;

import refer.cfg.TrelloProperties;
import refer.gateway.AuthException;
import refer.gateway.GatewayException;
import refer.gateway.LoggingClientHttpRequestInterceptor;
import refer.gateway.trello.request.RequestTrelloCard;
import refer.gateway.trello.response.ResponseTrelloBoard;
import refer.gateway.trello.response.ResponseTrelloCard;
import refer.gateway.trello.response.ResponseTrelloList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class TrelloGateway {

    private static final String API_URL = "https://api.trello.com";

    private static final String QUERY_PARAM_KEY = "key";
    private static final String QUERY_PARAM_TOKEN = "token";

    private static final String API_PATH_TOKEN_MEMBER = "/1/token/{token}/member";

    private static final String API_PATH_MEMBER_BOARDS = "/1/members/{username}/boards";

    private static final String API_PATH_BOARD = "/1/boards/{idBoard}";
    private static final String API_PATH_BOARD_LISTS = "/1/boards/{idBoard}/lists";
    private static final String API_PATH_BOARD_CARDS = "/1/boards/{idBoard}/cards";

    private static final String API_PATH_LIST_CARDS = "/1/lists/{idList}/cards";

    private static final String API_PATH_CARD = "/1/cards/{idCard}";

    private final TrelloProperties trelloProperties;

    private final RestTemplate restTemplate;

    @Autowired
    public TrelloGateway(TrelloProperties trelloProperties,
                         RestTemplateBuilder restTemplateBuilder) {
        this.trelloProperties = trelloProperties;

        this.restTemplate = restTemplateBuilder
                .additionalInterceptors(new LoggingClientHttpRequestInterceptor())
                .build();
    }

    public Optional<Auth> auth() {
        String key = trelloProperties.getIdentity().getKey();
        String token = trelloProperties.getIdentity().getToken();
        String username;
        try {
            username = requestUsernameForToken(key, token);
        } catch (Exception exception) {
            return Optional.empty();
        }

        Auth result = new Auth();
        result.setKey(key);
        result.setToken(token);
        result.setUsername(username);

        return Optional.of(result);
    }

    private String requestUsernameForToken(String key, String token) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_TOKEN_MEMBER)
                        .queryParam(QUERY_PARAM_KEY, key)
                        .buildAndExpand(token)
                        .toUri())
                .build();

        ResponseEntity<ResponseMember> response = restTemplate.exchange(
                request,
                ResponseMember.class);

        ResponseMember responseBody = response.getBody();
        if (responseBody == null) {
            throw new GatewayException("Response body is null");
        }

        return responseBody.getUsername();
    }

    public <X> X withAuth(Function<Auth, X> routine) {
        return auth().map(routine)
                .orElseThrow(() ->
                        new AuthException("Trello authentication failed"));
    }

    public List<ResponseTrelloBoard> indexBoards(Auth auth) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_MEMBER_BOARDS)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(auth.getUsername())
                        .toUri())
                .build();

        ResponseEntity<List<ResponseTrelloBoard>> response = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<List<ResponseTrelloBoard>>() {
                });

        return response.getBody();
    }

    public ResponseTrelloBoard showBoard(Auth auth, String idBoard) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_BOARD)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(idBoard)
                        .toUri())
                .build();

        ResponseEntity<ResponseTrelloBoard> response = restTemplate.exchange(
                request,
                ResponseTrelloBoard.class);

        return response.getBody();
    }

    public List<ResponseTrelloList> indexBoardLists(Auth auth, String idBoard) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_BOARD_LISTS)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(idBoard)
                        .toUri())
                .build();

        ResponseEntity<List<ResponseTrelloList>> response = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<List<ResponseTrelloList>>() {
                });

        return response.getBody();
    }

    public List<ResponseTrelloCard> indexBoardCards(Auth auth, String idBoard) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_BOARD_CARDS)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(idBoard)
                        .toUri())
                .build();

        ResponseEntity<List<ResponseTrelloCard>> response = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<List<ResponseTrelloCard>>() {
                });

        return response.getBody();
    }

    public List<ResponseTrelloCard> indexListCards(Auth auth, String idList) {
        RequestEntity<?> request = RequestEntity
                .get(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_LIST_CARDS)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(idList)
                        .toUri())
                .build();

        ResponseEntity<List<ResponseTrelloCard>> response = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<List<ResponseTrelloCard>>() {
                });

        return response.getBody();
    }

    public ResponseTrelloCard updateCard(Auth auth, String id, RequestTrelloCard card) {
        RequestEntity<RequestTrelloCard> request = RequestEntity
                .put(UriComponentsBuilder.fromHttpUrl(API_URL)
                        .path(API_PATH_CARD)
                        .queryParam(QUERY_PARAM_KEY, auth.getKey())
                        .queryParam(QUERY_PARAM_TOKEN, auth.getToken())
                        .buildAndExpand(id)
                        .toUri())
                .body(card);

        ResponseEntity<ResponseTrelloCard> response = restTemplate.exchange(
                request,
                ResponseTrelloCard.class);

        return response.getBody();
    }

    public static class Auth {

        private String key;

        private String token;

        private String username;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
