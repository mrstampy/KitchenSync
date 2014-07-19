package com.github.mrstampy.kitchensync.message.coretypes;

import java.io.Serializable;

public interface ReturnAddress extends Serializable {

	String getIpa();
	
	int getPort();
}
