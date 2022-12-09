package com.ashera.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.cocoa.NSAffineTransform;
import org.eclipse.swt.internal.cocoa.NSGraphicsContext;
import org.eclipse.swt.internal.cocoa.NSRect;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Control;

@Aspect
public class FillBackgroundAspect extends BaseAscpect {

	@Around("execution(* org.eclipse.swt.widgets.Control.fillBackground(..))")
	public Object processSystemRequest(final ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getTarget() instanceof Control) {
			Object[] args = pjp.getArgs();
			if (args.length == 7) {
				Control control = ((Control) pjp.getTarget());
				Image image = (Image) getFieldValueUsingReflection(control, "backgroundImage");
				
				Control bgcontrol = (Control) invokePrivateMethodUsingReflection(control, "findBackgroundControl");
				
				if (bgcontrol != null) {
					Image bgimage = (Image) getFieldValueUsingReflection(bgcontrol, "backgroundImage");
					
					if (bgimage != null && image == null) {
						return null;
					}
				}
				
				if (image != null && !image.isDisposed()) {
					NSGraphicsContext context = (NSGraphicsContext) args[1];
					NSRect rect = (NSRect) args[2];

					NSRect sourceRect = new NSRect();
					sourceRect.width = 0;
					sourceRect.height = 0;
					context.saveGraphicsState();
					context.setImageInterpolation(OS.NSImageInterpolationHigh);

					NSAffineTransform transform = NSAffineTransform.transform();
					transform.scaleXBy(1, -1);
					transform.translateXBy(0, -(rect.height + 2 * rect.y));
					transform.concat();
					image.handle.drawInRect(rect, sourceRect, OS.NSCompositeSourceOver, 1);
					context.restoreGraphicsState();
					return null;
				} 
			}
		}
		Object o = pjp.proceed();
		return o;
	}
}