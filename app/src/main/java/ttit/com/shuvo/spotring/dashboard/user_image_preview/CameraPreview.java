package ttit.com.shuvo.spotring.dashboard.user_image_preview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import ttit.com.shuvo.spotring.R;

public class CameraPreview extends AppCompatActivity {

//    Fotoapparat fotoapparat;
//    CameraView cameraView;
//
//    RelativeLayout cameraStateLayout;
//    ImageView cameraSwitch;
//    boolean isBackCamera = true;
//    ImageView cameraFlash;
//    boolean isFlashOn = false;
//
//    ImageView cameraClick;
//
//    RelativeLayout imagePreviewLayout;
//    ImageView picPreview;
//    ImageView picOk;
//    ImageView picCancel;
//
//    Bitmap finalBitmap;
//
//    private static BitmapCallBack bitmapCallback;
//
//    public static void setBitmapCallback(BitmapCallBack callback) {
//        bitmapCallback = callback;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera_preview);

//        cameraView = findViewById(R.id.camera_view_fp);
//        cameraView.setVisibility(View.VISIBLE);
//
//        cameraStateLayout = findViewById(R.id.image_capture_state_layout);
//        cameraStateLayout.setVisibility(View.VISIBLE);
//        cameraSwitch = findViewById(R.id.camera_switch);
//        cameraFlash = findViewById(R.id.camera_flash_button);
//
//        cameraClick = findViewById(R.id.camera_click);
//        cameraClick.setVisibility(View.VISIBLE);
//
//        imagePreviewLayout = findViewById(R.id.image_preview_layout);
//        imagePreviewLayout.setVisibility(View.GONE);
//
//        picPreview = findViewById(R.id.saved_picture_preview);
//        picOk = findViewById(R.id.picture_check_ok);
//        picCancel = findViewById(R.id.picture_check_cancel);
//
//        fotoapparat = Fotoapparat.with(this)
//                .into(cameraView)
//                .flash(FlashSelectorsKt.off())
//                .lensPosition(LensPositionSelectorsKt.back())
//                .previewScaleType(ScaleType.CenterInside)
//                .cameraErrorCallback(e -> {
//                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .build();
//
//        cameraSwitch.setOnClickListener(view -> {
//            isBackCamera = !isBackCamera;
//            fotoapparat.switchTo(isBackCamera ? LensPositionSelectorsKt.back() : LensPositionSelectorsKt.front(),
//                    CameraConfiguration.standard());
//        });
//
//        cameraFlash.setOnClickListener(view -> {
//            isFlashOn = !isFlashOn;
//            cameraFlash.setImageResource(isFlashOn ? R.drawable.flash_on_24 : R.drawable.flash_off_24);
//            fotoapparat.updateConfiguration(
//                    UpdateConfiguration.builder()
//                            .flash(isFlashOn ? FlashSelectorsKt.on() : FlashSelectorsKt.off()) // Turn on flash
//                            .build());
//        });
//
//        cameraClick.setOnClickListener(view ->
//                fotoapparat.takePicture()
//                        .toBitmap()
//                        .whenAvailable(bitmapPhoto -> {
//                            if (bitmapPhoto != null) {
//                                runOnUiThread(() -> {
//                                    cameraView.setVisibility(View.GONE);
//                                    cameraStateLayout.setVisibility(View.GONE);
//                                    cameraClick.setVisibility(View.GONE);
//
//                                    imagePreviewLayout.setVisibility(View.VISIBLE);
//                                    System.out.println(bitmapPhoto.rotationDegrees);
//                                    finalBitmap = rotateAndFlipBitmap(bitmapPhoto.bitmap, bitmapPhoto.rotationDegrees, !isBackCamera);
//
//                                    picPreview.setImageBitmap(finalBitmap);
//
////                                fotoapparat.switchTo(LensPositionSelectorsKt.back(),
////                                        CameraConfiguration.standard());
//
//                                });
//                            }
//                            else {
//                                runOnUiThread(()->Toast.makeText(CameraPreview.this, "Failed to capture image!", Toast.LENGTH_SHORT).show());
//                            }
//                            return Unit.INSTANCE;
//                        }));
//
//        picCancel.setOnClickListener(view -> {
//            cameraView.setVisibility(View.VISIBLE);
//            cameraStateLayout.setVisibility(View.VISIBLE);
//            cameraClick.setVisibility(View.VISIBLE);
////            fotoapparat.switchTo(isBackCamera ? LensPositionSelectorsKt.back() : LensPositionSelectorsKt.front(),
////                    CameraConfiguration.standard());
//
//            imagePreviewLayout.setVisibility(View.GONE);
//        });
//
//        picOk.setOnClickListener(view -> {
//            if (bitmapCallback != null) {
//                bitmapCallback.onBitmapReceived(finalBitmap);
//                finish();// Pass bitmap to MainActivity
//            }
//            else {
//                Toast.makeText(this, "Image Size is too big.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                finish();
//            }
//        });
    }

//    public Bitmap rotateAndFlipBitmap(Bitmap bitmap, int rotation, boolean isFrontCamera) {
//        Matrix matrix = new Matrix();
//        System.out.println(isFrontCamera);
//        // Rotate the image
//        if (rotation != 0) {
//            matrix.postRotate(-rotation);
//        }
//
//        // Flip for Front Camera (Mirror Effect)
//        if (isFrontCamera) {
//            matrix.preScale(1, -1);
//        }
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        fotoapparat.start();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        fotoapparat.stop();
//    }
}