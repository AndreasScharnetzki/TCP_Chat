package implementation;

public class TestRunApplication {

    public static void main(String[] args) {

        ChatUIImpl chatUI = new ChatUIImpl();
        try {
            chatUI.runUI(System.in, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
