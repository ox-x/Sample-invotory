package com.example.uhf.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uhf.R;
import com.example.uhf.activity.UHFMainActivity;

/**
 * Dashboard Fragment - 主菜单界面.
 * Shows four feature cards in a 2x2 grid: 借还, 入库, 仓库, 高级选项.
 * Uses Material3 styled cards with feature-specific colors.
 */
public class DashboardFragment extends KeyDwonFragment {

    private UHFMainActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (UHFMainActivity) getActivity();
        mContext.currentFragment = this;

        View view = getView();

        // Setup card click listeners (background colors are set in XML)
        setupCard(view, R.id.btnCheckout, CheckoutFragment.class, false);
        setupCard(view, R.id.btnStockIn, StockInFragment.class, true);
        setupCard(view, R.id.btnWarehouse, WarehouseFragment.class, false);
        setupCard(view, R.id.btnAdvanced, AdvancedFragment.class, true);
    }

    private void setupCard(View parent, int cardId, final Class<?> fragmentClass, final boolean requiresPassword) {
        View card = parent.findViewById(cardId);
        card.setOnClickListener(v -> {
            if (requiresPassword) {
                String featureLabel = getFeatureLabel(cardId);
                showPasswordDialog(featureLabel, fragmentClass);
            } else {
                mContext.openFeature(fragmentClass, "");
            }
        });
    }

    private String getFeatureLabel(int cardId) {
        if (cardId == R.id.btnCheckout) return getString(R.string.tab_borrow_return);
        if (cardId == R.id.btnStockIn) return getString(R.string.tab_stockin);
        if (cardId == R.id.btnWarehouse) return getString(R.string.tab_warehouse);
        if (cardId == R.id.btnAdvanced) return getString(R.string.tab_advanced);
        return "";
    }

    // ==================== Password Dialog ====================

    private static final String ADMIN_PASSWORD = "aaa";

    private void showPasswordDialog(String featureLabel, Class<?> targetFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("管理员验证");
        builder.setMessage("请输入管理员密码以进入" + featureLabel);

        final EditText etPassword = new EditText(mContext);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setHint("请输入密码");
        etPassword.setSingleLine(true);

        LinearLayout wrapper = new LinearLayout(mContext);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(48, 16, 48, 0);
        wrapper.addView(etPassword);
        builder.setView(wrapper);

        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String input = etPassword.getText().toString().trim();
                if (ADMIN_PASSWORD.equals(input)) {
                    dialog.dismiss();
                    mContext.openFeature(targetFragment, featureLabel);
                } else {
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }
            });
        });

        dialog.show();
    }

    @Override
    public void myOnKeyDwon() {
        // No action on dashboard
    }

    // ==================== Menu Item Model ====================
    // Used by AdvancedFragment

    public static class MenuItem {
        public String icon;
        public String label;
        public int color;
        public Class<?> fragmentClass;
        public boolean requiresPassword;

        public MenuItem(String icon, String label, int color, Class<?> fragmentClass) {
            this(icon, label, color, fragmentClass, false);
        }

        public MenuItem(String icon, String label, int color, Class<?> fragmentClass, boolean requiresPassword) {
            this.icon = icon;
            this.label = label;
            this.color = color;
            this.fragmentClass = fragmentClass;
            this.requiresPassword = requiresPassword;
        }
    }
}
