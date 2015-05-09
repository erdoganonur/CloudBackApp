package cloudback.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cloudback.activity.R;

public class BackupListArrayAdapter extends ArrayAdapter<Backup>
{
	private final Context context;
	ArrayList<Backup> backupList;

	public BackupListArrayAdapter(Context context, int textViewResourceId,
			ArrayList<Backup> objects)
	{
		super(context, textViewResourceId, objects);
		this.context = context;
		this.backupList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textViewName = (TextView) rowView.findViewById(R.id.label_name);
		TextView textViewRev = (TextView) rowView.findViewById(R.id.label_rev);
		textViewName.setText(backupList.get(position).getBackupName());
		textViewRev.setText(""+backupList.get(position).getRevNum());

		return rowView;
	}

}
