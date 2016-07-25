package com.capstone.authorfollow.authors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.authorfollow.R;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AuthorListAdapter extends RecyclerView.Adapter<AuthorListAdapter.ViewHolder> {
    private List<AuthorFollow> authorFollowList;
    private Callback listener;

    public AuthorListAdapter(Callback listener) {
        this.listener = listener;
        this.authorFollowList = new ArrayList<>();
    }

    public void setAuthors(List<AuthorFollow> authorFollowList) {
        this.authorFollowList.clear();
        if (null != authorFollowList) {
            this.authorFollowList.addAll(authorFollowList);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout listItemView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_author_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(listItemView);
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAuthorClick(authorFollowList.get(viewHolder.getAdapterPosition()));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AuthorFollow authorFollow = authorFollowList.get(position);
        holder.authorNameView.setText(authorFollow.getName());
        List<UpcomingBook> booksFromAuthorList = DBHelper.getBooksFromAuthor(authorFollow.getName());
        holder.bookCountView.setText(generateBookCountText(booksFromAuthorList, holder.bookCountView.getContext()));
        Picasso.with(holder.authorImgView.getContext()).load(authorFollow.getImageUrl()).placeholder(R.drawable.ic_movie_placeholder).into(holder.authorImgView);
    }

    private String generateBookCountText(List<UpcomingBook> booksFromAuthorList, Context context) {
        if (null != booksFromAuthorList && !booksFromAuthorList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int size = booksFromAuthorList.size();
            sb.append(size).append(" ");
            sb.append(size == 1 ? context.getString(R.string.book_count_text_single) : context.getString(R.string.book_count_text));
            return sb.toString();
        } else {
            return context.getString(R.string.book_count_text_none);
        }
    }

    @Override
    public int getItemCount() {
        return (null != authorFollowList ? authorFollowList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.author_list_avatar)
        CircleImageView authorImgView;

        @Bind(R.id.author_name)
        TextView authorNameView;

        @Bind(R.id.book_count)
        TextView bookCountView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Callback {
        void onAuthorClick(AuthorFollow author);
    }
}
