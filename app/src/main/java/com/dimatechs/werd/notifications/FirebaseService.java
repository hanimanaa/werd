package com.dimatechs.werd.notifications;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService  extends FirebaseInstanceIdService {
    
    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String user = "0523856567";
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        if(user != null)
        {
            updateToken(tokenRefresh);
        }
        
    }

    private void updateToken(String tokenRefresh) {
        String user = "0523856567";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user).setValue(token);
    }
}
