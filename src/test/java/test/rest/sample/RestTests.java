package test.rest.sample;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * Created by pshynin on 11/10/15.
 */
public class RestTests {

  @Test
  public void testCreateIssue() throws IOException {
    //get old list(set list) of bug reports
    Set<Issue> oldIssues = getIssues();
    //create new object = call constructor Issue class
    Issue newIssue = new Issue().withSubject("Test issue").withDescription("New Test Issue");
    //rebuild to return issueID
    int issueId = createIssue(newIssue);
    //get new list(set list) of bug reports)
    Set<Issue> newIssues = getIssues();
    oldIssues.add(newIssue.withId(issueId));
    //check old and new lists
    assertEquals(newIssues, oldIssues);
  }

  private Set<Issue> getIssues() throws IOException {
    /*
    for Auth use Executor where you send request
    create method Executor which accepts API key
    assign responce into local var jason
    */
    String json = getExecutor().execute(Request.Get("http://demo.bugify.com/api/issues.json"))
            .returnContent().asString();
    /*
    prepare transformation json to Set
    parse text received from server and get json element parsed
    */
    JsonElement parsed = new JsonParser().parse(json);
    /*
    from json element using key and get a needed part
    get list and assign to local var
    */
    JsonElement issues = parsed.getAsJsonObject().get("issues");
    //transform list received into Set
    return new Gson().fromJson(issues, new TypeToken<Set<Issue>>() {
    }.getType());
  }

  private Executor getExecutor() {
    return Executor.newInstance().auth("LSGjeU4yP1X493ud1hNniA==", "");
  }

  private int createIssue(Issue newIssue) throws IOException {
    //pack pairs into request
    String json = getExecutor().execute(Request.Post("http://demo.bugify.com/api/issues.json")
            .bodyForm(new BasicNameValuePair("subject", newIssue.getSubject()),
                    new BasicNameValuePair("description", newIssue.getDescription())))
            .returnContent().asString();
    //analize
    JsonElement parsed = new JsonParser().parse(json);
    //get parameter by key and return as int
    return parsed.getAsJsonObject().get("issue_id").getAsInt();
  }
}
