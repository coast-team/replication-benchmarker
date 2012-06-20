/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.json.moulinette.attributs;

/**
 *
 * @author romain
 */
public enum  Type {
    //Liste exhaustive des opérations possibles sur des fichiers ; add_dir, del_dir sont en suppléments
    update_file,add_file,clone_of_file,del_file,remain_file,change_type,unmerged_file,unknown,add_dir,del_dir
}
