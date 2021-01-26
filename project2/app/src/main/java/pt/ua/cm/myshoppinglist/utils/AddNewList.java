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

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;

public class AddNewList extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private String listId;
    private EditText newListText;
    private Button newListSaveButton;

    private FirebaseDbHandler db;

    public AddNewList() {
    }

    public static AddNewList newInstance(){
        return new AddNewList();
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

        View view = inflater.inflate(R.layout.new_list, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newListText = requireView().findViewById(R.id.newListText);
        newListSaveButton = getView().findViewById(R.id.newListButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();

        if(bundle != null){
            isUpdate = true;
            String listName = bundle.getString("listName");
            listId = bundle.getString("listId");
            newListText.setText(listName);
            assert listName != null;
            if(listName.length()>0)
                newListSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }

        MainActivity activity = (MainActivity) getActivity();

        db = activity.getDb();

        newListText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newListSaveButton.setEnabled(false);
                    newListSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newListSaveButton.setEnabled(true);
                    newListSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newListSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newListText.getText().toString();
                if(finalIsUpdate){
                    db.editList(listId, text);
                }
                else {
                    db.addList(text);
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
