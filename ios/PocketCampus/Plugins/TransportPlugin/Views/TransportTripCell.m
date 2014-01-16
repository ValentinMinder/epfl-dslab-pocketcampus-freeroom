/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */






//  Created by Loïc Gardiol on 27.11.13.



#import "TransportTripCell.h"

#import "TransportService.h"

#import "TransportUtils.h"

@interface TransportTripCell ()

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) IBOutlet UILabel* depTimeLabel;
@property (nonatomic, strong) IBOutlet UILabel* arrTimeLabel;
@property (nonatomic, strong) IBOutlet UILabel* durationLabel;
@property (nonatomic, strong) IBOutlet UILabel* changesLabel;
@property (nonatomic, strong) IBOutlet UILabel* firstLineLabel;

@end

@implementation TransportTripCell

@synthesize reuseIdentifier = _reuseIdentifier;

#pragma mark - Init

- (id)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"TransportTripCell" owner:nil options:nil];
    self = (TransportTripCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.depTimeLabel.text = nil;
        self.arrTimeLabel.text = nil;
        self.durationLabel.text = nil;
        self.changesLabel.text = nil;
        self.firstLineLabel.text = nil;
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    return [self initWithReuseIdentifier:reuseIdentifier];
}

#pragma mark - Properties

- (void)setTrip:(TransportTrip *)trip {
    if (trip) {
        //nil is allowed
        [PCUtils throwExceptionIfObject:trip notKindOfClass:[TransportTrip class]];
    }
    _trip = trip;
    
    TransportConnection* firstConnection = self.trip.firstConnection;
    NSString* depTimeString = nil;
    if (firstConnection) {
        depTimeString = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
    } else {
        depTimeString = [TransportUtils hourMinutesStringForTimestamp:self.trip.departureTime/1000.0];
    }
    self.depTimeLabel.text = depTimeString;
    self.arrTimeLabel.text = [TransportUtils hourMinutesStringForTimestamp:self.trip.arrivalTime/1000.0];
    self.durationLabel.text = [TransportUtils durationgStringForInterval:((self.trip.arrivalTime/1000.0) - (self.trip.departureTime/1000.0))];
    self.changesLabel.text = [NSString stringWithFormat:@"%lu", self.trip.numberOfChanges];
    self.firstLineLabel.text = firstConnection.line.shortName;
    
    static UIColor* normalColor = nil;
    static UIColor* leftColor = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        normalColor = [UIColor colorWithWhite:0.2 alpha:1.0];
        leftColor = [UIColor colorWithWhite:0.6 alpha:1.0];
    });
    self.depTimeLabel.textColor = firstConnection.hasLeft ? leftColor : normalColor;
}

@end
