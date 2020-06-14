/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects;

import java.awt.event.ActionListener;

/**
 *
 * @author Timur
 */
public interface EventSource {
    

    void notifyListeners();
    void addListener(ActionListener listener);
}
