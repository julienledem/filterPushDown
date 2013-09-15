package filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {
    Pusher pusher = new Pusher();
    Parser parser = new Parser();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String l;
    while ((l = br.readLine()) != null) {
      try {
        System.out.println(pusher.pushDown(parser.parse(l)));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
