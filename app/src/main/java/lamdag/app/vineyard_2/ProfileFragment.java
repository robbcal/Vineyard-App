package lamdag.app.vineyard_2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ProfileFragment extends Fragment{

    View view;
    private TextView enjoyText;
    private Button btnSignup;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        enjoyText = (TextView) view.findViewById(R.id.signup_text);
        btnSignup = (Button) view.findViewById(R.id.btn_signup);

        return view;
    }
}
