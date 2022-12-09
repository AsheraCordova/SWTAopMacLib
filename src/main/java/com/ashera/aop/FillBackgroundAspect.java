package com.ashera.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.cocoa.NSGraphicsContext;
import org.eclipse.swt.internal.cocoa.NSRect;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Control;

@Aspect
public class FillBackgroundAspect extends BaseAscpect{

    @Around("execution(* org.eclipse.swt.widgets.Control.fillBackground(..))")
    public Object processSystemRequest(final ProceedingJoinPoint pjp)
            throws Throwable {
    	if (pjp.getTarget() instanceof Control) {
	    	Object[] args = pjp.getArgs();
			if (args.length == 7) {
				Control control = ((Control) pjp.getTarget());
	    		Image image = (Image) getFieldValueUsingReflection(control, "backgroundImage");

	    		
	    		if (image != null && !image.isDisposed()) {
		    		NSGraphicsContext context = (NSGraphicsContext) args[1];
		    		NSRect rect  = (NSRect) args[2];


		    		NSRect sourceRect = new NSRect();
		    		sourceRect.width = image.getImageData().width;
		    		sourceRect.height = image.getImageData().height;
		    		
	    			context.saveGraphicsState();
	    			image.handle.drawInRect(rect, sourceRect, OS.NSCompositeCopy, 1);

	    			context.restoreGraphicsState();
	    			return null;
	    		}
	    	}
    	}
        Object o = pjp.proceed();
        return o;
    }
    
}