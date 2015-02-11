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

//  Created by Loïc Gardiol on 25.11.13.

#import <UIKit/UIKit.h>

typedef enum {
    TransportNextDeparturesCellStateLoading = 0,
    TransportNextDeparturesCellStateLoaded,
    TransportNextDeparturesCellStateError
} TransportNextDeparturesCellState;

@class TransportStation;
@class QueryTripsResult;

@interface TransportNextDeparturesCell : UITableViewCell

- (id)initWithReuseIdentifier:(NSString*)identifier;

/**
 * Use this property for accessiblity when tripResult is not set yet:
 * allows to speech "Next departures from <departureStation> to <destinationStation>"
 * IMPORTANT: 
 * - assigning new value does NOT nil tripResult
 * - accessibility labels will be bad before setting tripResult if you don't set this value
 * Default: nil
 */
@property (nonatomic, strong) TransportStation* departureStation;

/**
 * Use this property to temporarily indicate the destination when the QueryTripsResult is not available yet
 * The cell will display destinationStation.name
 * IMPORTANT: assigning new value nils tripResult
 * Default: nil
 */
@property (nonatomic, strong) TransportStation* destinationStation;

/**
 * Use this property in conjunction with destinationStation to indicate the state
 * If TransportNextDeparturesCellStateLoading, the cell displays a loading indication
 * If TransportNextDeparturesCellStateError, the cell displays the message "Error"
 * Otherwise, shows nothing in particular (you should not set TransportNextDeparturesCellStateLoaded manually)
 * Default: TransportNextDeparturesCellStateLoading
 */
@property (nonatomic) TransportNextDeparturesCellState state;

/**
 * Assigning a non-nil QueryTripResult sets self.state set to TransportNextDeparturesCellStateLoaded,
 * self.departureStation returns tripResult.from, and self.destinationStation returns tripResult.to
 * The cell then shows the content of tripResult
 * Default: nil
 */
@property (nonatomic, strong) QueryTripsResult* tripResult;

@end
