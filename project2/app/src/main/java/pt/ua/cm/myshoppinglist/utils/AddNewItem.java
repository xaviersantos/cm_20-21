package pt.ua.cm.myshoppinglist.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseUser;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.ui.lists.ListModel;

public class AddNewItem extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newItemText;
    private Button newItemSaveButton;
    private String itemId;

    private FirebaseDbHandler db;

    public static AddNewItem newInstance(){
        return new AddNewItem();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_item, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newItemText = requireView().findViewById(R.id.newItemText);
        newItemSaveButton = getView().findViewById(R.id.newItemButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String item = bundle.getString("item");
            itemId = bundle.getString("id");
            newItemText.setText(item);
            assert item != null;
            if(item.length()>0)
                newItemSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }

        MainActivity activity = (MainActivity) getActivity();

        db = activity.getDb();

        newItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newItemSaveButton.setEnabled(false);
                    newItemSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newItemSaveButton.setEnabled(true);
                    newItemSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newItemSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newItemText.getText().toString();
                if(finalIsUpdate){
                    db.editItem("listName", itemId, text);
                }
                else {
                    //ListModel item = new ListModel();
                    //item.setItem(text);
                    //item.setStatus(false);
                    db.addItem("listName", text);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
