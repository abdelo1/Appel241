package com.example.appel241.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appel241.R;
import com.example.appel241.User;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
	List<User> userList;
	Context mcontext;

	public RecyclerViewAdapter(Context context, List<User>userList){
		this.mcontext=context;
		this.userList=userList;


	}


	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user,viewGroup,false);
		return new RecyclerViewAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

		final User muser=userList.get(i);
		viewHolder.name.setText(muser.getName());
		viewHolder.number.setText(muser.getNumber());
		viewHolder.appeler.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View v) {
				String dial = "tel:" + muser.getNumber();
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(dial));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mcontext.startActivity(intent);
			}
		});



	}
	public  void updateList(List <User> newList){
		userList=new ArrayList<>();
		userList.addAll(newList);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return userList.size();
	}



	public class ViewHolder extends RecyclerView.ViewHolder {


		TextView name;
		TextView number;
		ImageView appeler;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			name=itemView.findViewById(R.id.name);
			number=itemView.findViewById(R.id.numero);
			appeler=itemView.findViewById(R.id.appeler);

		}

	}
}
