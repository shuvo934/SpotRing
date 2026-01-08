package ttit.com.shuvo.spotring.dashboard.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.dashboard.interfaces.PictureChooseListener;


public class ImageTakerChoiceDialog extends AppCompatDialogFragment {

    ImageView close;
    AlertDialog dialog;

    CardView camera;
    CardView gallery;

    private PictureChooseListener pictureChooseListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PictureChooseListener)
            pictureChooseListener = (PictureChooseListener) getActivity();

        View view = inflater.inflate(R.layout.image_taker_choice_dialog, null);

        close = view.findViewById(R.id.close_picture_choice);
        camera = view.findViewById(R.id.camera_choose_from_choice);
        gallery = view.findViewById(R.id.gallery_choose_from_choice);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        close.setOnClickListener(v -> {
            if (pictureChooseListener != null)
                pictureChooseListener.onPictureChoose(0);
            dialog.dismiss();
        });

        camera.setOnClickListener(view1 -> {
            if (pictureChooseListener != null)
                pictureChooseListener.onPictureChoose(1);
            dialog.dismiss();
        });

        gallery.setOnClickListener(view1 -> {
            if (pictureChooseListener != null)
                pictureChooseListener.onPictureChoose(2);
            dialog.dismiss();
        });

        return dialog;
    }
}
