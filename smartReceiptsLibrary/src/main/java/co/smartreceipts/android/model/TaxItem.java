package co.smartreceipts.android.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.text.TextUtils;

import co.smartreceipts.android.settings.UserPreferenceManager;
import co.smartreceipts.android.settings.catalog.UserPreference;
import co.smartreceipts.android.utils.log.Logger;

public class TaxItem {

	private static final int SCALE = 2;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
	
	private BigDecimal mPercent;
	private BigDecimal mPrice, mTax;
	private boolean mUsePreTaxPrice;
	
	public TaxItem(String percent, UserPreferenceManager preferences) {
		try {
			mPercent = new BigDecimal(percent);
		}
		catch (NumberFormatException e) {
			mPercent = null;
		}
		mUsePreTaxPrice = preferences.get(UserPreference.Receipts.UsePreTaxPrice);
	}
	
	public TaxItem(float percent, UserPreferenceManager preferences) {
		mPercent = new BigDecimal(percent);
		mUsePreTaxPrice = preferences.get(UserPreference.Receipts.UsePreTaxPrice);
	}
	
	public TaxItem(BigDecimal percent, UserPreferenceManager preferences) {
		mPercent = percent;
		mUsePreTaxPrice = preferences.get(UserPreference.Receipts.UsePreTaxPrice);
	}
	
	public TaxItem(BigDecimal percent, boolean usePreTaxPrice) {
		mPercent = percent;
		mUsePreTaxPrice = usePreTaxPrice;
	}
	
	public TaxItem(String percent, boolean usePreTaxPrice) {
		try {
			mPercent = new BigDecimal(percent);
		}
		catch (NumberFormatException e) {
			mPercent = null;
		}
		mUsePreTaxPrice = usePreTaxPrice;
	}
	
	public TaxItem(float percent, boolean usePreTaxPrice) {
		mPercent = new BigDecimal(percent);
		mUsePreTaxPrice = usePreTaxPrice;
	}
	
	public BigDecimal getPercent() {
		return mPercent;
	}
	
	public String getPercentAsString() {
		if (mPercent == null) {
			return "";
		}
		else {
			BigDecimal scaledPercent = mPercent.setScale(SCALE, ROUNDING_MODE);
			return getDecimalFormat().format(scaledPercent.doubleValue()) + "%";
		}
	}
	
	public void setPrice(String price) {
		if (TextUtils.isEmpty(price)) {
			mPrice = new BigDecimal(0);
			getTax();
		}
		try {
			mPrice = new BigDecimal(price.trim());
			getTax();
		}
		catch (NumberFormatException e) {
			Logger.error(this, e);
			mPrice = null;
		}
	}
	
	public boolean isValid() {
		return mTax != null;
	}
	
	public BigDecimal getTax() {
		if (mPercent == null || mPrice == null) {
			mTax = null;
		}
		else {
			Logger.debug(this, mPrice.toString());
			if (mUsePreTaxPrice) {
				mTax = mPrice.multiply(mPercent).divide(new BigDecimal(100), SCALE, ROUNDING_MODE);
			}
			else {
				mTax = mPrice.subtract(mPrice.divide(mPercent.divide(new BigDecimal(100), 10, ROUNDING_MODE).add(new BigDecimal(1)), SCALE, ROUNDING_MODE));
			}
		}
		return mTax;
	}
	
	@Override
	public String toString() {
		if (mTax == null) {
			return "";
		}
		else {
			return getDecimalFormat().format(mTax.doubleValue());
		}
	}
	
	private DecimalFormat getDecimalFormat() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		format.setGroupingUsed(false);
		return format;
	}

}
