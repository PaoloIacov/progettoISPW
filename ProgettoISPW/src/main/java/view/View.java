package view;

public interface View {
    void display();
    void close();
    void refresh();
    void back();
    void showError(String message);
}


