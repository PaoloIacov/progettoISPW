package model.domain;

import model.bean.CredentialsBean;

public class Credentials {

    // Metodo per validare le credenziali di base (controlla che non siano vuoti)
    public static boolean validate(CredentialsBean credentials) {
        if (credentials.getUsername() == null || credentials.getUsername().isEmpty()) {
            return false;
        }
        if (credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            return false;
        }
        return true;
    }
}

