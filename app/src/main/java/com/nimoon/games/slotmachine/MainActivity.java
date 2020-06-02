package com.nimoon.games.slotmachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {

    Button btnBuyCoin, btnRedeemCoin, btnFreeCoin, btnRoll;
    TextView txtCoin, txtUsername;
    EditText edtCoin;
    ViewFlipper viewFlipper1, viewFlipper2, viewFlipper3;

    int coin = 0;
    String username = "";

    ArrayList<String> arrNameImage = null;
    ArrayList<BlurImageView> arrImage1 = null;
    ArrayList<BlurImageView> arrImage2 = null;
    ArrayList<BlurImageView> arrImage3 = null;

    int posBefore1 = 0, posBefore2 = 0, posBefore3 = 0;
    int value1, value2, value3, posBlur;

    boolean stateItem = false;
    boolean rollComplete = false;

    String url1 = "https://nimoon.com/games/slotmachine/buy.php";
    String url2 = "https://nimoon.com/games/slotmachine/redeem.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        String arr[] = getResources().getStringArray(R.array.items);
        arrNameImage = new ArrayList<>(Arrays.asList(arr));

        initView();
        loadUsername();
        loadCoin();
        arrImage1 = createArrImage();
        arrImage2 = createArrImage();
        arrImage3 = createArrImage();

    }

    private ArrayList<BlurImageView> createArrImage() {
        ArrayList<BlurImageView> arrImage = new ArrayList<>();
        for (int i = 0; i < arrNameImage.size(); i++) {
            BlurImageView imageView = new BlurImageView(this);
            int idImage = getResources().getIdentifier(arrNameImage.get(i), "drawable", getPackageName());
            imageView.setImageResource(idImage);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(i + 1);
            imageView.setBlur(20);
            arrImage.add(imageView);
        }
        return arrImage;
    }

    private void loadCoin() {
        coin = Ultil.getPrefCoin(this);
        if (coin == -1) {
            coin = 1000;
            Ultil.setPrefsCoin(this, coin);
        }
        txtCoin.setText(coin + " COIN");
    }

    private void loadUsername() {
        username = Ultil.getPrefUsername(this);
        if (username.equals("")) {
            username = createRandomUsername();
            Ultil.setPrefsUsername(this, username);
        }
        txtUsername.setText(username);
    }

    private String createRandomUsername() {
        Random rnd = new Random();
        int n = 10000000 + rnd.nextInt(90000000);
        return "U-" + n;
    }

    private boolean checkInputCoin(String coin_pet) {
        if (edtCoin.length() == 0) {
            Toast.makeText(this, "hmmm", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                if (Integer.parseInt(coin_pet) > coin) {
                    Toast.makeText(this, "hmmm hmmm", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void createViewflipper(ViewFlipper viewFlipper, ArrayList<BlurImageView> arrImage) {
        viewFlipper.removeAllViews();
        Collections.shuffle(arrImage);
        for (BlurImageView imageView : arrImage) {
            viewFlipper.addView(imageView);
        }
    }

    private int calculateCoin(int r1, int r2, int r3, int coinBet) {
        if (r1 == r2 && r1 == r3) {
            if (r1 == 4) {
                return coinBet * 100;
            } else if (r1 == 7) {
                return coinBet * 40;
            } else {
                return coinBet * 20;
            }
        } else if (r1 == r2 || r1 == r3 || r2 == r3) {
            if (r1 == r2) {
                return calculateTemp(r1, coinBet);
            } else if (r1 == r3) {
                return calculateTemp(r1, coinBet);
            } else {
                return calculateTemp(r2, coinBet);
            }
        } else {
            return -coinBet;
        }
    }

    private int calculateTemp(int r, int coinBet) {
        if (r == 9)
            return coinBet * 10;
        else if (r == 8) {
            return coinBet * 2;
        } else {
            return 0;
        }
    }

    private void openDialogBuyCoin() {
        String[] items = new String[]{
                "1000 coin: $0.99",
                "5000 coin: $3.99",
                "15000 coin: $9.99"
        };
        new AlertDialog.Builder(this)
                .setTitle("Buy coin")
                .setCancelable(false)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0) {
                            openWebBrowser(url1 + "?user=" + username + "&coin=" + 1000);
                        } else if (selectedPosition == 1) {
                            openWebBrowser(url1 + "?user=" + username + "&coin=" + 5000);
                        } else {
                            openWebBrowser(url1 + "?user=" + username + "&coin=" + 15000);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openDialogRedeemCoin() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_dialog, null);

        final EditText editText = dialogView.findViewById(R.id.edtDialog);

        new AlertDialog.Builder(this)
                .setTitle("Redeem coin")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int redeem_coin = Integer.parseInt(editText.getText().toString());
                            if (redeem_coin > 0 && redeem_coin < coin) {
                                dialog.dismiss();
                                openWebBrowser(url2 + "?user=" + username + "&coin=" + redeem_coin);
                            } else {
                                Toast.makeText(MainActivity.this, "hmmmm", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openWebBrowser(String url) {
        Log.d("AAA", "openWebBrowser: " + url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void enableButton(boolean enable) {
        btnRoll.setEnabled(enable);
        btnBuyCoin.setEnabled(enable);
        btnFreeCoin.setEnabled(enable);
        btnRedeemCoin.setEnabled(enable);
    }

    private void initView() {
        btnBuyCoin = findViewById(R.id.btnBuyCoin);
        btnRedeemCoin = findViewById(R.id.btnRedeemCoin);
        btnFreeCoin = findViewById(R.id.btnFreeCoin);
        btnRoll = findViewById(R.id.btnRoll);
        txtCoin = findViewById(R.id.txtCoin);
        txtUsername = findViewById(R.id.txtUsername);
        edtCoin = findViewById(R.id.edtCoin);
        viewFlipper1 = findViewById(R.id.viewFlipper1);
        viewFlipper2 = findViewById(R.id.viewFlipper2);
        viewFlipper3 = findViewById(R.id.viewFlipper3);

        btnBuyCoin.setOnClickListener(this);
        btnRedeemCoin.setOnClickListener(this);
        btnFreeCoin.setOnClickListener(this);
        btnRoll.setOnClickListener(this);

        viewFlipper1.getInAnimation().setAnimationListener(this);
        viewFlipper2.getInAnimation().setAnimationListener(this);
        viewFlipper3.getInAnimation().setAnimationListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuyCoin:
                openDialogBuyCoin();
                break;
            case R.id.btnRedeemCoin:
                openDialogRedeemCoin();
                break;
            case R.id.btnFreeCoin:
                Toast.makeText(this, "Getting free coin...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRoll:
                if (checkInputCoin(edtCoin.getText().toString())) {
                    enableButton(false);

                    arrImage1.get(posBefore1).setBlur(20);
                    arrImage2.get(posBefore2).setBlur(20);
                    arrImage3.get(posBefore3).setBlur(20);

                    createViewflipper(viewFlipper1, arrImage1);
                    createViewflipper(viewFlipper2, arrImage2);
                    createViewflipper(viewFlipper3, arrImage3);

                    viewFlipper1.startFlipping();
                    viewFlipper2.startFlipping();
                    viewFlipper3.startFlipping();

                    new CountDownTimer(6000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.d("AAA", "onTick: " + millisUntilFinished);
                            if (millisUntilFinished > 2800 && millisUntilFinished < 3000) {
                                stateItem = true;
                                posBlur = 1;
                                viewFlipper1.stopFlipping();
                            } else if (millisUntilFinished > 1800 && millisUntilFinished < 2000) {
                                stateItem = true;
                                posBlur = 2;
                                viewFlipper2.stopFlipping();
                            }
                        }

                        @Override
                        public void onFinish() {
                            stateItem = true;
                            posBlur = 3;
                            rollComplete = true;
                            viewFlipper3.stopFlipping();
                        }
                    }.start();
                }
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (stateItem) {
            stateItem = false;

            posBefore1 = viewFlipper1.getDisplayedChild();
            posBefore2 = viewFlipper2.getDisplayedChild();
            posBefore3 = viewFlipper3.getDisplayedChild();

            Log.d("AAA", "tag1: " + arrImage1.get(posBefore1).getTag());
            Log.d("AAA", "tag2: " + arrImage2.get(posBefore2).getTag());
            Log.d("AAA", "tag3: " + arrImage3.get(posBefore3).getTag());

            value1 = (int) arrImage1.get(posBefore1).getTag();
            value2 = (int) arrImage2.get(posBefore2).getTag();
            value3 = (int) arrImage3.get(posBefore3).getTag();

            if (posBlur == 1) {
                arrImage1.get(posBefore1).setBlur(0);
            } else if (posBlur == 2) {
                arrImage2.get(posBefore2).setBlur(0);
            } else {
                arrImage3.get(posBefore3).setBlur(0);
            }

            if (rollComplete) {
                enableButton(true);
                rollComplete = false;
                int coinBet = Integer.parseInt(edtCoin.getText().toString());
                int coninResult = calculateCoin(value1, value2, value3, coinBet);

                coin += coninResult;
                Ultil.setPrefsCoin(this, coin);
                txtCoin.setText(coin + " COIN");

                if (coninResult > 0) {
                    Toast.makeText(this, "WIN " + coninResult + " COIN", Toast.LENGTH_SHORT).show();
                } else if (coninResult < 0) {
                    Toast.makeText(this, "LOSE " + (-coninResult) + " COIN", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SAME SAME :)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
