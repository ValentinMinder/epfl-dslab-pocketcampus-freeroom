



//  Created by Loïc Gardiol on 13.01.13.



#import "moodle.h"

@interface MoodleResource (Additions)<NSCopying>

/*
 * Returns last path component of iUrl
 * E.g. homework1.pdf
 */
@property (nonatomic, readonly) NSString* filename;

- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToMoodleResource:(MoodleResource*)moodleResource;
- (NSUInteger)hash;

@end

@interface MoodleSection (Additions)<NSCopying>

@end
