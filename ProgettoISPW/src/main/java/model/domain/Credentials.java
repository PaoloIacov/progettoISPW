package model.domain;

import model.bean.CredentialsBean;

public class Credentials {

    // Metodo per validare le credenziali di base (controlla che non siano vuoti)
    public static boolean validate(CredentialsBean credentials) {
        return credentials.getUsername() != null && !credentials.getUsername().isEmpty() &&
                credentials.getPassword() != null && !credentials.getPassword().isEmpty();
    }

}

