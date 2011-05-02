package org.pocketcampus.core.communication.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Declares a method which is part of the PCP API and can be called directly
 * from a PCP client (<i>i.e. a PocketCampus Action</i>).
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
}
