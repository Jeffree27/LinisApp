package com.example.jeffree.linisapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LinisRecyclerAdapter extends RecyclerView.Adapter<LinisRecyclerAdapter.ViewHolder> {

    public List<LinisPost> linis_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public LinisRecyclerAdapter(List<LinisPost> linis_list){

        this.linis_list = linis_list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linis_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String linisPostId = linis_list.get(position).LinisPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = linis_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = linis_list.get(position).getImage_url();
        //String thumbUri = linis_list.get(position).getImage_thumb();
        holder.setLinisImage(image_url);

        String user_id = linis_list.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);


                } else {

                    //String error = task.getException().getMessage();
                    //Toast.makeText(context.getApplicationContext(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });

        try {
            long millisecond = linis_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Count Likes
        firebaseFirestore.collection("Posts/" + linisPostId + "/Volunteers").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateVolunteersCount(count);

                } else {

                    holder.updateVolunteersCount(0);

                }

            }
        });


        //Get Likes

        firebaseFirestore.collection("Posts/" + linisPostId + "/Volunteers").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.linisVolunteerBtn.setImageDrawable(context.getDrawable(R.drawable.volunteer_plain));

                }else {

                    holder.linisVolunteerBtn.setImageDrawable(context.getDrawable(R.drawable.volunteer_colored));

                }

            }
        });

        //Volunteer feature
        holder.linisVolunteerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + linisPostId + "/Volunteers").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> volunteersMap = new HashMap<>();
                            volunteersMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + linisPostId + "/Volunteers").document(currentUserId).set(volunteersMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + linisPostId + "/Volunteers").document(currentUserId).delete();

                        }

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return linis_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView;

        private ImageView linisImageView;

        private TextView linisUserName;
        private CircleImageView linisUserImage;

        private TextView linisDate;

        private ImageView linisVolunteerBtn;
        private TextView linisVolunteerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            linisVolunteerBtn = mView.findViewById(R.id.linis_volunteer_btn);
            linisVolunteerCount = mView.findViewById(R.id.linis_volunteer_count);

        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.linis_desc);
            descView.setText(descText);

        }

        public void setLinisImage(String downloadUri){ //String thumbUri

            linisImageView = mView.findViewById(R.id.linis_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(linisImageView);
            //Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri.thumbnail({Glide.with(context).load(thumbUri)).into(linisImageView)

            //Glide.with(context).load(downloadUri).into(linisImageView);

        }

        public void setUserData(String name, String image){

            linisUserImage = mView.findViewById(R.id.linis_user_image);
            linisUserName = mView.findViewById(R.id.linis_user_name);

            linisUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.mipmap.action_blank_profile);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(linisUserImage);

        }

        public void setTime(String date) {

            linisDate = mView.findViewById(R.id.linis_date);
            linisDate.setText(date);

        }

        public void updateVolunteersCount(int count){

            linisVolunteerCount = mView.findViewById(R.id.linis_volunteer_count);
            linisVolunteerCount.setText(count + " Volunteers");

        }
    }
}
