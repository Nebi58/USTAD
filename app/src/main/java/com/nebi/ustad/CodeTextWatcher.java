package com.nebi.ustad;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

// Otomatik geçiş için TextWatcher sınıfı
public class CodeTextWatcher implements TextWatcher {
    private EditText currentEditText ,nextEditText , previousEditText;

    public CodeTextWatcher(EditText current, EditText next,EditText previous) {
        this.currentEditText = current;
        this.nextEditText = next;
        this.previousEditText = previous;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    // Değişiklik olduktan sonra çalışıyor ve sonraki edittext'e geçiyor veya öncekine geçiyor
    @Override
    public void afterTextChanged(Editable currentText) {
        if (currentText.length() == 1 && nextEditText != null) {
            nextEditText.requestFocus();
        }
        else if (currentText.length() == 0 && previousEditText != null) {
            previousEditText.requestFocus(); // Sildiğinde geri dön
        }
    }
}

/**

 * TextWatcher => EditText'de yazıyı yazarken veya değiştirirken dinlemeyi sağlar.
 * Burada kullanmamızın sebebi ise her bir harfi yazdığımızda bir sonrakine geçmesindendir

 */

