package controller;

import com.ug.project.controller.CommentController;
import com.ug.project.service.CommentService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentControllerTest {

    @Test
    void testAddComment(){
        CommentService service = new CommentService();
        CommentController controller = new CommentController(service);

        //String testContent = "This is a test comment.";
        String testContent = "This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests. This is a long test content string used for unit tests.";

        boolean flagComment = controller.addComment(testContent);

       System.out.println(String.format("Result of adding comment: %b", flagComment));
       assertTrue(flagComment, "Expected addComment to return true");

    }

    @Test
    void testGetFormattedComments(){
        CommentService service = new CommentService();
        CommentController controller = new CommentController(service);

        List<String> comments = controller.getFormattedComments();

        //assertNotNull(comments, "Expected non-null list of comments");
        assertNull(comments, "Expected non-null list of comments");

        System.out.println("Total formatted Comments: " + comments.size());
    }
}