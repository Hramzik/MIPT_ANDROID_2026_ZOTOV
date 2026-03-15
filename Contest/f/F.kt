import java.util.*

// Submission id 157667402
data class Animal(val id: Int, val type: Animal.Type, val height: Int, var keeperId: Int?) {
    enum class Type {
        CAT,
        DOG,
        HIPPO,
        HORSE,
        FISH
    }
}

data class Keeper(val id: Int, val name: String)

class ZooDatabase {
    private val idToAnimal = mutableMapOf<Int, Animal>()
    private val idToKeeper = mutableMapOf<Int, Keeper>()

    private val animalComparator = compareBy<Animal>({ it.height }, { it.id })
    private val animals = TreeSet(animalComparator)
    private val typeToAnimals = mutableMapOf<Animal.Type, TreeSet<Animal>>()
    private val talkingAnimals = TreeSet(animalComparator)
    private val keeperIdToAnimals = mutableMapOf<Int, TreeSet<Animal>>()
    private val keeperNameToAnimals = mutableMapOf<String, TreeSet<Animal>>()

    fun onQuery(line: String) {
        val words = line.split(' ')
        when (words[0]) {
            "ANIMAL_ADD" -> {
                val id = words[1].toInt()
                val type = Animal.Type.valueOf(words[2])
                val height = words[3].toInt()
                val animal = Animal(id, type, height, null)
                idToAnimal[id] = animal
                animals.add(animal)
                typeToAnimals.getOrPut(type) { TreeSet(animalComparator) }.add(animal)
                if (type != Animal.Type.FISH) talkingAnimals.add(animal)
            }
            "ANIMAL_DEL" -> {
                val animalId = words[1].toInt()
                unassignAnimal(animalId)

                val animal = idToAnimal.remove(animalId) ?: return
                animals.remove(animal)
                typeToAnimals[animal.type]?.remove(animal)
                if (animal.type != Animal.Type.FISH) talkingAnimals.remove(animal)
            }
            "KEEPER_ADD" -> {
                val id = words[1].toInt()
                val name = words[2]
                idToKeeper[id] = Keeper(id, name)
            }
            "ASSIGN" -> {
                val animalId = words[1].toInt()
                val keeperId = words[2].toInt()
                val animal = idToAnimal[animalId] ?: return
                val keeper = idToKeeper[keeperId] ?: return

                if (animal.keeperId == keeperId) return
                unassignAnimal(animalId)
                keeperIdToAnimals.getOrPut(keeperId) { TreeSet(animalComparator) }.add(animal)
                keeperNameToAnimals.getOrPut(keeper.name) { TreeSet(animalComparator) }.add(animal)
                animal.keeperId = keeperId
            }
            "UNASSIGN" -> {
                val animalId = words[1].toInt()
                unassignAnimal(animalId)
            }
            "Q_KEEPER" -> {
                val keeperId = words[1].toInt()
                printAnimalIds(keeperIdToAnimals[keeperId])
            }
            "Q_NAME" -> {
                val name = words[1]
                printAnimalIds(keeperNameToAnimals[name])
            }
            "Q_TALL" -> {
                val h = words[1].toInt()
                printAnimalIds(animals.tailSet(Animal(Int.MIN_VALUE, Animal.Type.CAT, h + 1, null)))
            }
            "Q_SOUND" -> {
                printAnimalIds(talkingAnimals)
            }
            "Q_TYPE" -> {
                val type = Animal.Type.valueOf(words[1])
                printAnimalIds(typeToAnimals[type])
            }
        }
    }

    private fun printAnimalIds(animals: SortedSet<Animal>?) {
        if (animals.isNullOrEmpty()) println("NONE")
        else println(animals.joinToString(" ") { it.id.toString() })
    }

    private fun unassignAnimal(animalId: Int) {
        val animal = idToAnimal[animalId] ?: return
        animal.keeperId?.let { keeperId ->
            keeperIdToAnimals[keeperId]?.remove(animal)
            idToKeeper[keeperId]?.let { keeper -> keeperNameToAnimals[keeper.name]?.remove(animal) }
        }

        animal.keeperId = null
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val q = scanner.nextInt()
    scanner.nextLine()
    val zooDatabase = ZooDatabase()
    repeat(q) {
        val line = scanner.nextLine()
        zooDatabase.onQuery(line)
    }
}
