package com.darrenmleith.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BlogActivity extends AppCompatActivity {

    private DatabaseReference _mDatabase;
    private RecyclerView _blogRecycler;
    private FirebaseAuth _mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        _blogRecycler = findViewById(R.id.blogRecyclerView);
        _blogRecycler.setHasFixedSize(true);
        _blogRecycler.setLayoutManager(new LinearLayoutManager(this));
        _mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        _mAuth = FirebaseAuth.getInstance();

    }

    //fire up the onStart method to set the FirebaseRecyclerAdapter that is tied to a custom object representing a "Blog" and a RecyclerView.ViewHolder object
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class, //custom class, built entirely with getters/setters and a default constructor, public Blog() {}
                R.layout.blog_row, //custom CardView giving us greater control over what we are presenting. This is the ONLY place we reference this CardView !
                BlogViewHolder.class, //custom RecyclerView.ViewHolder
                _mDatabase //this all works because the Blog.class follows the same format as what we have stored in this location
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setDescription(blog.getDescription());
                blogViewHolder.setImage(getApplicationContext(), blog.getImageURL());
            }
        };
        _blogRecycler.setAdapter(firebaseRecyclerAdapter);
    }


    //need to extend RecyclerView.ViewHolder with a custom class that is tied to the FirebaseRecyclerAdapter
    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        //create a View object and assign it the value of itemView
        View blogView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            blogView = itemView;
        }
        //set the title text of postTitle within the CardView

        public void setTitle(String title) {
            TextView postTitle = blogView.findViewById(R.id.postTitle);
            postTitle.setText(title);
        }
        //set the title text of postTitle within the CardView
        public void setDescription(String title) {
            TextView postDescription = blogView.findViewById(R.id.postDescription);
            postDescription.setText(title);
        }

        //set the image that is within the CardView using Picasso
        public void setImage(Context context, String imageURL) {
            ImageView imageView = blogView.findViewById(R.id.postImage);
            Picasso.get().load(imageURL).into(imageView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post) {
        startActivity(new Intent(BlogActivity.this, PostActivity.class));
        } else if (item.getItemId() == R.id.logout) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        // Firebase sign out
        _mAuth.signOut();

        // Google sign out
        LoginActivity.mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(BlogActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        _mAuth.signOut();

        // Google revoke access
        LoginActivity.mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(BlogActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
}