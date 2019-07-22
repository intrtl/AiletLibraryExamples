import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';
 
// We are importing the native Java module here
import {NativeModules} from 'react-native';
var IrModule = NativeModules.IrModule;
 
type Props = {};
export default class App extends Component<Props> {
 
  // async function to call the Java native method
  test() {
    IrModule.reports("q");
  }
 
  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={ this.test }>
              <Text>Invoke native Java code2</Text>
         </TouchableOpacity>
      </View>
    );
  }
}
 
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  }
});
