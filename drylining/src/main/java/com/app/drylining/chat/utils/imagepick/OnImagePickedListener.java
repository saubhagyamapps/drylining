package com.app.drylining.chat.utils.imagepick;

import java.io.File;

public interface OnImagePickedListener {

    void onImagePicked(int requestCode, File file);

    void onImagePickError(int requestCode, Exception e);

    void onImagePickClosed(int requestCode);
}
