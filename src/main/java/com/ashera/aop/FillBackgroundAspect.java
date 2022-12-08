package com.ashera.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.cocoa.NSBezierPath;
import org.eclipse.swt.internal.cocoa.NSColor;
import org.eclipse.swt.internal.cocoa.NSGraphicsContext;
import org.eclipse.swt.internal.cocoa.NSPoint;
import org.eclipse.swt.internal.cocoa.NSRect;
import org.eclipse.swt.internal.cocoa.NSView;
import org.eclipse.swt.widgets.Control;

@Aspect
public class FillBackgroundAspect extends BaseAscpect{

    @Around("execution(* org.eclipse.swt.widgets.Control.fillBackground(..))")
    public Object processSystemRequest(final ProceedingJoinPoint pjp)
            throws Throwable {
    	if (pjp.getTarget() instanceof Control) {
	    	Object[] args = pjp.getArgs();
			if (args.length == 7) {
				Control thisControl = ((Control) pjp.getTarget());
	    		Control control = (Control)invokePrivateMethodUsingReflection(thisControl, "findBackgroundControl");

	    		if (control == null) {
	    			control = thisControl;
	    		}
	    		Image image = (Image) getFieldValueUsingReflection(control, "backgroundImage");

	    		
	    		if (image != null && !image.isDisposed()) {
		    		NSView view = (NSView) args[0]; 
		    		NSGraphicsContext context = (NSGraphicsContext) args[1];
		    		NSRect rect  = (NSRect) args[2];
		    		int imgHeight  = (int)args[3]; 
		    		NSView gcView  = (NSView) args[4]; 
		    		int tx  = (int)args[5]; 
		    		int ty = (int)args[6];

	    			context.saveGraphicsState();
	    			NSColor.colorWithPatternImage(image.handle).setFill();
	    			NSPoint phase = new NSPoint();
	    			NSView controlView = control.view;
	    			if (!controlView.isFlipped()) {
	    				phase.y = controlView.bounds().height;
	    			}
	    			if (imgHeight == -1) {
	    				NSView contentView = controlView.superview();
	    				phase = controlView.convertPoint_toView_(phase, contentView);
	    				phase.y = contentView.bounds().height - phase.y;
	    			} else {
	    				phase = view.convertPoint_toView_(phase, controlView);
	    				Image backgroundImage = (Image) getFieldValueUsingReflection(thisControl, "backgroundImage");
	    				phase.y += imgHeight - backgroundImage.getBounds().height;
	    			}
	    			if (gcView != null) {
	    				NSPoint pt = gcView.convertPoint_toView_(new NSPoint(), view);
	    				phase.x += pt.x;
	    				phase.y -= pt.y;
	    			}
	    			phase.x -= tx;
	    			phase.y += ty;
	    			context.setPatternPhase(phase);
	    			NSBezierPath.fillRect(rect);
	    			context.restoreGraphicsState();
	    			return null;
	    		}
	    	}
    	}
        Object o = pjp.proceed();
        return o;
    }
    
}