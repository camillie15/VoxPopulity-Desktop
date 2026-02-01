import com.ug.project.controller.PostController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostControllerTest {

    private final PostController postController = new PostController();

    @Test
    public void savePostTest (){
        String content = "Test de contenido";
        String title = "Test de titulo";
        int idCommunity = 0;
        assertTrue(postController.savePost(title,content,idCommunity));
    }

    @Test
    public void savePostTestWithTitleEmpty (){
        String content = "Test de contenido";
        String title = "";
        int idCommunity = 0;
        assertFalse(postController.savePost(title,content,idCommunity));
    }

    @Test
    public void getPostsCurrentUserEquals(){
        assertEquals(List.of(), postController.getPostsCurrentUser());
    }

    @Test
    public void getPostsCurrentUserNotEquals(){
        assertNotEquals(List.of(), postController.getPostsCurrentUser());
    }

}
