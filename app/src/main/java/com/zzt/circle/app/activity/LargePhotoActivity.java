package com.zzt.circle.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.adapter.CommentsAdapter;
import com.zzt.circle.app.entity.CommentEntity;
import com.zzt.circle.app.entity.HotspotEntity;
import com.zzt.circle.app.net.GetComments;
import com.zzt.circle.app.net.GetHotspot;
import com.zzt.circle.app.net.PostComment;
import com.zzt.circle.app.tools.DisplayUtils;
import com.zzt.circle.app.widget.HotSpotButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzt on 15-5-25.
 */
public class LargePhotoActivity extends ActionBarActivity {
    private static final int HOTSPOT_SIZE = 20; //单位dp
    private static final int COMMENTS_LIST_MAX_WIDTH = 200; //单位dp
    private float x;
    private float y;
    private ImageView ivLargeImage;
    private String photoURL;
    private RelativeLayout layout;
    private List<HotspotEntity> hotspotList = new ArrayList<HotspotEntity>();
    private String account;
    private String token;
    private int msgID;
    private LinearLayout inputBar;
    private EditText etComment;
    private ImageButton btnSend;
    private ListView lvComments;
    private CommentsAdapter commentsAdapter;
    private int parentWidth;
    private int parentHeight;
    private int photoWidth;
    private int photoHeight;
    private boolean hasMeasured;
    private int hotSpotID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = Config.getCachedAccount(this);
        token = Config.getCachedToken(this);
        msgID = getIntent().getIntExtra(Config.KEY_MSG_ID, 0);
        photoURL = getIntent().getStringExtra(Config.KEY_PHOTO_URL);

        ImageLoader imageLoader = ImageLoader.getInstance();
        setContentView(R.layout.activity_large_photo);

        layout = (RelativeLayout) findViewById(R.id.relativeRoot);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!hasMeasured) {
                    parentWidth = layout.getWidth();
                    parentHeight = layout.getHeight();
                    hasMeasured = true;
                }
                return hasMeasured;
            }
        });

        ivLargeImage = (ImageView) findViewById(R.id.ivLargePhoto);
        imageLoader.displayImage(Config.SERVER_URL + photoURL, ivLargeImage);

        vto = ivLargeImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                photoWidth = ivLargeImage.getWidth();
                photoHeight = ivLargeImage.getHeight();
                return true;
            }
        });

        commentsAdapter = new CommentsAdapter(this);

        lvComments = (ListView) findViewById(R.id.lvComments);
        lvComments.setAdapter(commentsAdapter);

        inputBar = (LinearLayout) findViewById(R.id.inputBar);

        etComment = (EditText) findViewById(R.id.etComment);

        btnSend = (ImageButton) findViewById(R.id.btnSend);

        loadHotSpot();


        ivLargeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initSize();
                        x = event.getX();
                        y = event.getY();
                        break;
                }
                return false;
            }
        });

        ivLargeImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputBar.setVisibility(View.GONE);
                inputBar.setVisibility(View.VISIBLE);
                return true;
            }
        });
        ivLargeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputBar.setVisibility(View.GONE);
                lvComments.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etComment.getText())) {
                    Toast.makeText(LargePhotoActivity.this, R.string.comment_can_not_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                String comment = etComment.getText().toString();

                new PostComment(account, token, comment, Math.round(x * 100 / photoWidth), Math.round(y * 100 / photoHeight), msgID, new PostComment.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        etComment.setText(null);
                        clearHotSpot();
                        loadHotSpot();
                        commentsAdapter.clear();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvComments.getLayoutParams();
                        loadCommentbyHotSpotID(hotSpotID, params);
                        Toast.makeText(LargePhotoActivity.this, R.string.comment_success, Toast.LENGTH_LONG).show();
                    }
                }, new PostComment.FailCallback() {
                    @Override
                    public void onFail() {
                        onFail(Config.RESULT_STATUS_FAIL);
                    }

                    @Override
                    public void onFail(int code) {
                        switch (code) {
                            case Config.RESULT_STATUS_FAIL:
                                Toast.makeText(LargePhotoActivity.this, R.string.comment_failed, Toast.LENGTH_LONG).show();
                                break;
                            case Config.RESULT_STATUS_INVALID_TOKEN:
                                Toast.makeText(LargePhotoActivity.this, R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private void loadHotSpot() {

        new GetHotspot(account, token, msgID, 1, 20, new GetHotspot.SuccessCallback() {
            @Override
            public void onSuccess(List<HotspotEntity> hotspots) {
                hotspotList.addAll(hotspots);
                drawHotSpot();
            }
        }, new GetHotspot.FailCallback() {
            @Override
            public void onFail() {
                Toast.makeText(LargePhotoActivity.this, R.string.comment_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void drawHotSpot() {
        for (final HotspotEntity hotspot : hotspotList) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(DisplayUtils.dp2px(LargePhotoActivity.this, HOTSPOT_SIZE), DisplayUtils.dp2px(LargePhotoActivity.this, HOTSPOT_SIZE));
            lp.addRule(RelativeLayout.ALIGN_LEFT, ivLargeImage.getId());
            lp.addRule(RelativeLayout.ALIGN_TOP, ivLargeImage.getId());
            lp.leftMargin = hotspot.getX() * photoWidth / 100 - lp.width / 2;
            lp.topMargin = hotspot.getY() * photoHeight / 100 - lp.height / 2;
            final HotSpotButton btn = new HotSpotButton(this, hotspot.getHotspotID());
            btn.setId(btn.getHotspotID());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    x = hotspot.getX() * photoWidth / 100;
                    y = hotspot.getY() * photoHeight / 100;
                    LargePhotoActivity.this.hotSpotID = btn.getHotspotID();
                    if (hotspot.getY() >= 50)
                        showCommentsByHotspotID(btn.getHotspotID(), true);
                    else
                        showCommentsByHotspotID(btn.getHotspotID(), false);
                }
            });
            layout.addView(btn, lp);
        }
    }

    private void clearHotSpot() {
        for (HotspotEntity hotspot : hotspotList) {
            HotSpotButton btn = (HotSpotButton) findViewById(hotspot.getHotspotID());
            layout.removeView(btn);
        }
    }

    private void showCommentsByHotspotID(int hotspotID, final boolean top) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
        if (top)
            params.topMargin = 0;
        else
            params.topMargin = parentHeight / 2;
        loadCommentbyHotSpotID(hotspotID, params);
        inputBar.setVisibility(View.VISIBLE);
        lvComments.setVisibility(View.VISIBLE);
    }

    private void loadCommentbyHotSpotID(int hotSpotID, final RelativeLayout.LayoutParams params) {
        final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.now_loading), getString(R.string.please_waite));
        new GetComments(account, token, hotSpotID, 1, 10, new GetComments.SuccessCallback() {
            @Override
            public void onSuccess(List<CommentEntity> comments) {
                pd.dismiss();
                lvComments.setLayoutParams(params);
                commentsAdapter.clear();
                commentsAdapter.addAll(comments);
            }
        }, new GetComments.FailCallback() {
            @Override
            public void onFail() {
                onFail(Config.RESULT_STATUS_FAIL);
            }

            @Override
            public void onFail(int code) {
                pd.dismiss();
                switch (code) {
                    case Config.RESULT_STATUS_FAIL:
                        Toast.makeText(LargePhotoActivity.this, R.string.loading_failed, Toast.LENGTH_LONG).show();
                        break;
                    case Config.RESULT_STATUS_INVALID_TOKEN:
                        Toast.makeText(LargePhotoActivity.this, R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LargePhotoActivity.this, LoginActivity.class));
                        break;
                }
            }
        });
    }

    private void initSize() {
        photoWidth = ivLargeImage.getWidth();
        photoHeight = ivLargeImage.getHeight();
        parentWidth = layout.getWidth();
        parentHeight = layout.getHeight();
    }
}
