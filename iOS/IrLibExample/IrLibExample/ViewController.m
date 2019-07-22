//
//  ViewController.m
//  Test3
//
//  Created by Vsevolod Didkovskiy on 08/07/2019.
//  Copyright © 2019 Intelligence Retail. All rights reserved.
//

#import "ViewController.h"
#import <IrLib/IrLib.h>

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [IrView initRealm];
    
    //Configure notification observer
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reciveShareshelf:)
                                                 name:@"broadcast_SHARESHELF"
                                               object:nil];
}

//Do something with recived notification
- (void)reciveShareshelf:(NSNotification*)notification {
    NSDictionary *info = notification.userInfo;
    NSString *visit_id = @"";
    NSString *external_visit_id = @"";
    if ([info objectForKey:@"VISIT_ID"]){
        visit_id = [info objectForKey:@"VISIT_ID"];
    }
    if ([info objectForKey:@"EXTERNAL_VISIT_ID"]){
        external_visit_id = [info objectForKey:@"EXTERNAL_VISIT_ID"];
    }
    //Do something then finish recive all visit reports
}

- (IBAction)buttonClick:(id)sender {
    //Init IrView with user name, password and notification identificator
    long res = [IrView init:@"username"
                   password:@"password"
               notification:@"broadcast"];
    
    if (res == IR_RESULT_OK){//If result ok do...
        //Start camera with external store id and visit id.
        [IrView start:self
    external_store_id:@"123456789"
    external_visit_id:@"testVisit"];
    }
}

@end
