package net.minecraft.src;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class HttpUtilRunnable implements Runnable {
	final IProgressUpdate feedbackHook;
	final String sourceURL;
	final Map field_76177_c;
	final File destinationFile;
	final IDownloadSuccess downloadSuccess;
	final int maxFileSize;

	HttpUtilRunnable(IProgressUpdate var1, String var2, Map var3, File var4, IDownloadSuccess var5, int var6) {
		this.feedbackHook = var1;
		this.sourceURL = var2;
		this.field_76177_c = var3;
		this.destinationFile = var4;
		this.downloadSuccess = var5;
		this.maxFileSize = var6;
	}

	public void run() {
		URLConnection var1 = null;
		InputStream var2 = null;
		DataOutputStream var3 = null;
		if(this.feedbackHook != null) {
			this.feedbackHook.resetProgressAndMessage("Downloading Texture Pack");
			this.feedbackHook.resetProgresAndWorkingMessage("Making Request...");
		}

		try {
			byte[] var4 = new byte[4096];
			URL var5 = new URL(this.sourceURL);
			var1 = var5.openConnection();
			float var6 = 0.0F;
			float var7 = (float)this.field_76177_c.entrySet().size();
			Iterator var8 = this.field_76177_c.entrySet().iterator();

			while(var8.hasNext()) {
				Entry var9 = (Entry)var8.next();
				var1.setRequestProperty((String)var9.getKey(), (String)var9.getValue());
				if(this.feedbackHook != null) {
					++var6;
					this.feedbackHook.setLoadingProgress((int)(var6 / var7 * 100.0F));
				}
			}

			var2 = var1.getInputStream();
			var7 = (float)var1.getContentLength();
			int var28 = var1.getContentLength();
			if(this.feedbackHook != null) {
				this.feedbackHook.resetProgresAndWorkingMessage(String.format("Downloading file (%.2f MB)...", new Object[]{Float.valueOf(var7 / 1000.0F / 1000.0F)}));
			}

			if(this.destinationFile.exists()) {
				long var29 = this.destinationFile.length();
				if(var29 == (long)var28) {
					this.downloadSuccess.onSuccess(this.destinationFile);
					if(this.feedbackHook != null) {
						this.feedbackHook.onNoMoreProgress();
					}

					return;
				}

				System.out.println("Deleting " + this.destinationFile + " as it does not match what we currently have (" + var28 + " vs our " + var29 + ").");
				this.destinationFile.delete();
			}

			var3 = new DataOutputStream(new FileOutputStream(this.destinationFile));
			if(this.maxFileSize > 0 && var7 > (float)this.maxFileSize) {
				if(this.feedbackHook != null) {
					this.feedbackHook.onNoMoreProgress();
				}

				throw new IOException("Filesize is bigger than maximum allowed (file is " + var6 + ", limit is " + this.maxFileSize + ")");
			}

			boolean var30 = false;

			while(true) {
				int var31 = var2.read(var4);
				if(var31 < 0) {
					this.downloadSuccess.onSuccess(this.destinationFile);
					if(this.feedbackHook != null) {
						this.feedbackHook.onNoMoreProgress();
					}
					break;
				}

				var6 += (float)var31;
				if(this.feedbackHook != null) {
					this.feedbackHook.setLoadingProgress((int)(var6 / var7 * 100.0F));
				}

				if(this.maxFileSize > 0 && var6 > (float)this.maxFileSize) {
					if(this.feedbackHook != null) {
						this.feedbackHook.onNoMoreProgress();
					}

					throw new IOException("Filesize was bigger than maximum allowed (got >= " + var6 + ", limit was " + this.maxFileSize + ")");
				}

				var3.write(var4, 0, var31);
			}
		} catch (Throwable var26) {
			var26.printStackTrace();
		} finally {
			try {
				if(var2 != null) {
					var2.close();
				}
			} catch (IOException var25) {
			}

			try {
				if(var3 != null) {
					var3.close();
				}
			} catch (IOException var24) {
			}

		}

	}
}
