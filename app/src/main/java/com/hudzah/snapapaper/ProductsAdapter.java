//package com.hudzah.snapapaper;
//
//import android.content.Context;
//import android.transition.AutoTransition;
//import android.transition.TransitionManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.billingclient.api.SkuDetails;
//
//import org.w3c.dom.Text;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{
//
//    private static final String TAG = "ProductsAdapter";
//    private OnPurchaseListener mOnPurchaseListener;
//
//    private List<SkuDetails> skuDetailsList;
//    private Context context;
//
//    public ProductsAdapter(Context context, List<SkuDetails> skuDetailsList, OnPurchaseListener onPurchaseListener) {
//        this.skuDetailsList = skuDetailsList;
//        this.context = context;
//        this.mOnPurchaseListener = onPurchaseListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_listitem, parent, false);
//        ViewHolder holder = new ViewHolder(view, mOnPurchaseListener);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.productHeader.setText(skuDetailsList.get(position).getTitle());
//        holder.productEmote.setText("");
//        holder.productPrice.setText("1.99");
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return skuDetailsList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        public CardView cardView;
//        public Button dropdownButton;
//        public TextView productHeader;
//        public TextView productEmote;
//        public TextView productPrice;
//        public LinearLayout linear;
//        public TextView productDesc1;
//        public TextView productDesc2;
//        public TextView productDesc3;
//        OnPurchaseListener onPurchaseListener;
//
//        public ViewHolder(@NonNull View itemView, OnPurchaseListener onPurchaseListener) {
//            super(itemView);
//
//            this.onPurchaseListener = onPurchaseListener;
//
//            cardView = itemView.findViewById(R.id.cardView);
//            dropdownButton = itemView.findViewById(R.id.dropdownButton);
//            productHeader = itemView.findViewById(R.id.productHeader);
//            productEmote = itemView.findViewById(R.id.productEmote);
//            productPrice = itemView.findViewById(R.id.productPrice);
//            linear = itemView.findViewById(R.id.linear);
//            productDesc1 = itemView.findViewById(R.id.productDesc1);
//            productDesc2 = itemView.findViewById(R.id.productDesc2);
//            productDesc3 = itemView.findViewById(R.id.productDesc3);
//
//            cardView.setOnClickListener(this);
//            dropdownButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(linear.getVisibility() == View.GONE){
//
//                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
//                        linear.setVisibility(View.VISIBLE);
//                        dropdownButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//                    }
//                    else{
//
//                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
//                        linear.setVisibility(View.GONE);
//                        dropdownButton.setBackgroundResource(R.drawable.arrow_bitmap);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onClick(View v) {
//            onPurchaseListener.onPurchaseClick(getAdapterPosition(), skuDetailsList);
//        }
//    }
//
//
//    public interface OnPurchaseListener{
//
//        void onPurchaseClick(int position, List<SkuDetails> skuDetailsList);
//    }
//
//}
