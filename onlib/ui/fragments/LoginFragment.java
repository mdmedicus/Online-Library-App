package com.md.onlib.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.md.onlib.MainActivity;
import com.md.onlib.R;

public class LoginFragment extends Fragment {

    boolean login = false;
    int sign_in = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myinfo", "LoginFragment onCreate");


    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("myinfo", "LoginFragment onCreateView");
        final View root = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText username = (EditText) root.findViewById(R.id.username_login);
        final EditText password = (EditText) root.findViewById(R.id.password);


        Button loginButton = (Button) root.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        Log.i("myinfo", "login tıklanıldı");

                        String username_string = String.valueOf(username.getText());
                        String password_string = String.valueOf(password.getText());

                        ((MainActivity)getActivity()).connection.user_pass = username_string + "/" + password_string;
                        ((MainActivity)getActivity()).connection.login_positive = true;
                        ((MainActivity)getActivity()).connection.wait = true;

                        while(((MainActivity)getActivity()).connection.wait){

                        }


                        if (((MainActivity)getActivity()).isOnline()){
                            if (username_string.length() >= 5 && username_string.length() <= 20
                                    && password_string.length() >= 8 && password_string.length() <= 30
                                    && username_string.matches("[a-zA-Z0-9]*") && password_string.matches("[a-zA-Z0-9]*")) {


                                if (((MainActivity)getActivity()).connection.isLogin) {
                                    Log.i("myinfo", "Başarıyla giriş yapıldı.");
                                    Navigation.findNavController(root).navigate(R.id.action_login_to_study);

                                } else {
                                    Log.i("myinfo", "Kullanıcı adı veya parola hatalı.");
                                    new AlertDialog.Builder(((MainActivity) getActivity()))
                                            .setMessage("Kullanıcı adı veya parola hatalı.").show();
                                }
                            } else {
                                Log.i("myinfo", "Kullanıcı adı veya şifre uygun değil");
                                new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Kurallar")
                                        .setMessage("Kullanıcı adı 5-20 karakter arasında sadece harfler ve rakamlar içerebilir. \n" +
                                                "Şifre 8-30 karakter arasında sadece harfler ve rakamlar içerebilir.").show();

                            }
                        } else {
                            Log.i("myinfo", "Şu anda online değil.");
                            new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                                    .setMessage("Internet bağlantınızı kontrol edin.").show();
                        }

            }
        });


     Button register_button = (Button) root.findViewById(R.id.register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("myinfo", "register tıklanıldı");
                String username_string = String.valueOf(username.getText());
                String password_string = String.valueOf(password.getText());

                if (((MainActivity)getActivity()).isOnline()){
                    if (username_string.length() >= 5 && username_string.length() <= 20
                            && password_string.length() >= 8 && password_string.length() <= 30
                            && username_string.matches("[a-zA-Z0-9]*") && password_string.matches("[a-zA-Z0-9]*")) {


                        ((MainActivity)getActivity()).connection.user_pass = username_string + "/" + password_string;
                        ((MainActivity)getActivity()).connection.register_positive = true;
                        ((MainActivity)getActivity()).connection.wait = true;

                        while(((MainActivity)getActivity()).connection.wait){

                        }

                        if (((MainActivity)getActivity()).connection.isLogin) {
                            Log.i("myinfo", "Başarıyla giriş yapıldı.");
                            Navigation.findNavController(root).navigate(R.id.action_login_to_study);
                        } else {
                            Log.i("myinfo", "Kullanıcı adı veya parola hatalı.");
                            new AlertDialog.Builder(((MainActivity) getActivity()))
                                    .setMessage("Kullanıcı adı veya parola hatalı.").show();
                        }
                    } else {
                        Log.i("myinfo", "Kullanıcı adı veya şifre uygun değil");
                        new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Kurallar")
                                .setMessage("Kullanıcı adı 5-20 karakter arasında sadece harfler ve rakamlar içerebilir. \n" +
                                        "Şifre 8-30 karakter arasında sadece harfler ve rakamlar içerebilir.").show();

                    }
                } else {
                    Log.i("myinfo", "Şu anda online değil.");
                    new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                            .setMessage("Internet bağlantınızı kontrol edin.").show();
                }

            }
        });



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("myinfo", "LoginFragment onViewCreated");

        while(!((MainActivity)getActivity()).connection.firstLoop){

        }

        if(((MainActivity)getActivity()).online == false & ((MainActivity)getActivity()).connection.localDatabase.getUsername() != null){

            Navigation.findNavController(view).navigate(R.id.action_login_to_study);
        }else {



            if (((MainActivity) getActivity()).connection.isLogin) {
                Log.i("myinfo", "Başarıyla giriş yapıldı.");
                Navigation.findNavController(view).navigate(R.id.action_login_to_study);
            }

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println(((MainActivity)getActivity()).online);
        ((MainActivity)getActivity()).refresh_floating_visibility(false);

    }
}
