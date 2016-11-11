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
 * Created by pshynin on 11/10/16.
 */
public class RestTests {

  @Test
  public void testCreateIssue() throws IOException {
    //получаем старый список(множество) баг репортов
    Set<Issue> oldIssues = getIssues();
    //создаем новый объект = вызываем конструктор класса Issue
    Issue newIssue = new Issue().withSubject("Test issue").withDescription("New Test Issue");
    //переделываем чтоб возвращал issueId
    int issueId = createIssue(newIssue);
    //получаем новый список(множество)
    Set<Issue> newIssues = getIssues();
    oldIssues.add(newIssue.withId(issueId));
    //срасниваем списки(множества)
    assertEquals(newIssues, oldIssues);
  }

  private Set<Issue> getIssues() throws IOException {
    //для Auth используется Executor в который передается запрос
    //создаем метод Executor который принимает API key
    //присваиваем responce в локальную переменную jason
    String json = getExecutor().execute(Request.Get("http://demo.bugify.com/api/issues.json"))
            .returnContent().asString();
    //подготавливаем преобразование json в множество
    //parse текс полученный от сервера и на выходе получаем json элемент parsed
    JsonElement parsed = new JsonParser().parse(json);
    //из json элемента по ключу извлекаем нужную часть
    //получаем нужный список и присваиваем в локальную переменную
    JsonElement issues = parsed.getAsJsonObject().get("issues");
    //и теперь полученный список issues преобразовываем в множество типа issues
    return new Gson().fromJson(issues, new TypeToken<Set<Issue>>() {
    }.getType());
  }

  private Executor getExecutor() {
    return Executor.newInstance().auth("LSGjeU4yP1X493ud1hNniA==", "");
  }

  private int createIssue(Issue newIssue) throws IOException {
    //набор пар кодируем и упаковываем в запрос
    String json = getExecutor().execute(Request.Post("http://demo.bugify.com/api/issues.json")
            .bodyForm(new BasicNameValuePair("subject", newIssue.getSubject()),
                    new BasicNameValuePair("description", newIssue.getDescription())))
            .returnContent().asString();
    //анализируем строчку
    JsonElement parsed = new JsonParser().parse(json);
    //берем значение по ключу и возвращием его как целое число getAsInt()
    return parsed.getAsJsonObject().get("issue_id").getAsInt();
  }
}
