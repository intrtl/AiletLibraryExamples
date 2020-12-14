//
//  ViewController.m
//  IrDeeplinkExample
//
//  Created by Vsevolod Didkovskiy on 14.12.2020.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)tapStartVisitButton:(id)sender {
    NSURLComponents *components = [NSURLComponents new];
    components.scheme = @"intelligenceretail";
    NSMutableArray *queryItems = [NSMutableArray new];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"method" value:@"visit"]];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"login" value:@"testUser"]];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"password" value:@"testPassword"]];
//    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"user_id" value:@""]];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"store_id" value:@"1"]];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"visit_id" value:@"testVisitId"]];
    [queryItems addObject: [NSURLQueryItem queryItemWithName:@"back_url_scheme" value:@"IrDeeplinkExample"]];
    
    components.queryItems = queryItems;
    if (components.URL != nil){
        UIApplication *application = [UIApplication sharedApplication];
        [application openURL:components.URL options:@{} completionHandler:^(BOOL success) {
            if (success) {
                NSLog(@"Ir Opened");
            }
        }];
    }
}

@end
