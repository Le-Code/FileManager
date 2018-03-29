package com.example.filetransport;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.VideoView;

/**
 * Created by yaojian on 2017/11/9.
 */

public class SecondActivity extends AppCompatActivity {

    private VideoView vv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two);
        btn = (Button) findViewById(R.id.btn);
        final View root = View.inflate(this,R.layout.popu,null);
        vv = root.findViewById(R.id.vv);
        vv.setVideoPath("/storage/emulated/0/.estrongs/recycle/1509005603151/storage/emulated/0/Music/es_recycle_content/战l2[高清抢先版].mp4");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this)
                        .setView(root);
                AlertDialog dialog = builder.create();
                dialog.show();
                vv.start();
            }
        });

    }
}
