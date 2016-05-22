package com.bessmertniyy.nikita.testwork.db.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bessmertniyy.nikita.testwork.R;
import com.bessmertniyy.nikita.testwork.db.tables.LinkTable;

/**
 * Created by Working on 20.05.2016.
 */
public class LinkTableAdapter extends CursorAdapter{

    private LayoutInflater cursorLayoutInflater;

    public LinkTableAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);

        cursorLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorLayoutInflater.inflate(R.layout.link_table_adapter_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final String linkURL = cursor.getString(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_URL));
        final int linkId = cursor.getInt(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_ID));
        int linkStatus = cursor.getInt(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_STATUS));
        final long linkDate = cursor.getInt(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_ADD_DATE));

        LinearLayout linkLinearLayout = (LinearLayout)view.findViewById(R.id.link_table_adapter_row_link_linear_layout);
        TextView linkTextView = (TextView)view.findViewById(R.id.link_table_adapter_row_link_textview);
        linkTextView.setText(linkURL);

        switch(linkStatus){
            case 0:
                linkTextView.setTextColor(context.getResources().getColor(R.color.colorGreenHistoryTab));
                break;
            case 1:
                linkTextView.setTextColor(Color.RED);
                break;
            case 2:
                linkTextView.setTextColor(Color.GRAY);
                break;
            default:linkTextView.setTextColor(Color.GRAY);
        }
        linkLinearLayout.setClickable(true);
        linkTextView.setClickable(true);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApplicationB(cursor, context, linkURL, linkId, linkDate);

//                if(cursor.getInt(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_STATUS)) == 0){
//                    openLink(context, linkURL, linkId, linkDate, true);
//                }else{
//                    openLink(context, linkURL, linkId, linkDate, false);
//                }

            }
        });
        linkLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApplicationB(cursor, context, linkURL, linkId, linkDate);
            }
        });

    }

    private void openApplicationB(Cursor cursor, Context context, String linkURL, int linkId, long linkDate){
        if(cursor.getInt(cursor.getColumnIndex(LinkTable.TABLE_LINK_COLUMN_STATUS)) == 0){
            openLink(context, linkURL, linkId, linkDate, true);
        }else{
            openLink(context, linkURL, linkId, linkDate, false);
        }
    }

    private void openLink(Context context, String linkURL, int linkId, long linkDate, boolean isGreenLink){

        Intent openAppB = new Intent("com.bessmertniyy.nikita.applicationb.LINK_VIEW_ACTIVITY");
        openAppB.putExtra("linkURL", linkURL);

        if(isGreenLink) {
            openAppB.putExtra("isForDelete", true);
        }else{
            openAppB.putExtra("isForUpdate", true);
        }
        openAppB.putExtra("linkId", linkId);
        openAppB.putExtra("linkDate", linkDate);

        context.startActivity(openAppB);

    }

}
