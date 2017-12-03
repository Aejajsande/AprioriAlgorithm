import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Apriori {
	float support;
	float confidence;

	public Apriori(float sup, float conf) {
		this.support = sup;
		this.confidence = conf;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter number between 1 to 5 to select the Kmart database ");
		int database = sc.nextInt();

		System.out.println("Enter the minimum support in percentage: ");
		float sup = sc.nextFloat();
		System.out.println("Enter the minimum confidence in percentage: ");
		float conf = sc.nextFloat();
		Apriori ap = new Apriori(sup, conf);
		List<List> mainList = new ArrayList<List>();
		Set itemsSet = new TreeSet();
		try {
			Scanner scan = new Scanner(new FileReader("C:\\APP\\DataTransaction" + database + ".txt"));

			while (scan.hasNextLine()) {
				List tempList = new ArrayList();

				String s = scan.nextLine();
				String s1[] = s.split(" ");
				for (int i = 0; i < s1.length; i++) {
					tempList.add(s1[i]);
					itemsSet.add(s1[i]);
				}
				mainList.add(tempList);

			}
			System.out.println("Following are the transactions of selected Database");
			for (int i = 0; i < mainList.size(); i++) {
				System.out.println("T" + (i + 1) + "--->" + mainList.get(i));
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map singleFreqItemMap = ap.calculateSingleFrequent(mainList, itemsSet);
		Set<String> tempSingleFreqSet = singleFreqItemMap.keySet();
		List<String> tempSingleFreqList = new ArrayList<String>(tempSingleFreqSet);
		List<String> SecondItemList = ap.createSecondaryList(tempSingleFreqList);
		List<String> isSuppotedList = new ArrayList<String>();
		List<String> nonSupportedList = new ArrayList<String>();
		Map finalFreqMap = ap.recursiveFunction(SecondItemList, mainList, isSuppotedList, nonSupportedList,
				singleFreqItemMap);
		Set<String> freqKeys = finalFreqMap.keySet();
		System.out.println("Following are the frequent item sets");
		for (String s : freqKeys) {
			System.out.print(s + "----->");
			System.out.println(finalFreqMap.get(s));
		}
		System.out.println();
		ap.generateAssosiationRules(finalFreqMap, mainList);
	}

	public Map calculateSingleFrequent(List<List> transactList, Set<String> singleItemSet) {
		Map<String, Float> tempSet = new HashMap<String, Float>();
		for (String s : singleItemSet) {
			float count = 0;
			for (int j = 0; j < transactList.size(); j++) {
				if (transactList.get(j).contains(s)) {
					count++;
				}
			}
			if ((count * 100 / 20) >= this.support) {
				tempSet.put(s, count * 100 / 20);
			}
		}
		return tempSet;

	}

	public List<String> createSecondaryList(List<String> SingleFreqList) {
		List<String> tempList = new ArrayList<String>();
		for (int i = 0; i < SingleFreqList.size(); i++) {
			for (int j = 0; j < SingleFreqList.size(); j++) {
				if (j > i) {
					tempList.add(SingleFreqList.get(i) + "," + SingleFreqList.get(j));
				}
			}
		}
		return tempList;
	}

	public Map<String, Float> recursiveFunction(List<String> secondItemList, List<List> transaction,
			List<String> isSuppotedList, List<String> nonSupportedList, Map<String, Float> freqMap) {
		isSuppotedList.removeAll(isSuppotedList);
		int initial = isSuppotedList.size();

		for (int i = 0; i < secondItemList.size(); i++) {
			String tempArray[] = secondItemList.get(i).split(",");

			float count = 0;
			for (int j = 0; j < transaction.size(); j++) {
				boolean isPresent = true;
				for (int k = 0; k < tempArray.length; k++) {
					if (!transaction.get(j).contains(tempArray[k])) {
						isPresent = false;
					}
				}
				if (isPresent) {
					count++;
				}
			}
			if (count * 100 / 20 >= this.support) {
				isSuppotedList.add(secondItemList.get(i));
				freqMap.put(secondItemList.get(i), (count * 100 / 20));
			} else {
				nonSupportedList.add(secondItemList.get(i));
			}
		}
		if (isSuppotedList.size() != initial) {
			Set<String> itmSet = createListFromSupported(isSuppotedList, nonSupportedList);
			List<String> itmList = new ArrayList<String>(itmSet);
			itmList = checkRepetition(itmList);
			recursiveFunction(itmList, transaction, isSuppotedList, nonSupportedList, freqMap);
		} else {
		}
		return freqMap;
	}

	public Set<String> createListFromSupported(List<String> supported, List<String> unSupported) {
		Set<String> itmSet = new HashSet<String>();
		for (int i = 0; i < supported.size(); i++) {
			for (int j = 0; j < supported.size(); j++) {
				if (j > i) {
					String tempArray[] = supported.get(j).split(",");
					for (int k = 0; k < tempArray.length; k++) {
						if ((checkIfUnsupported(unSupported, supported.get(i) + "," + tempArray[k]))) {
							if (!supported.get(i).contains(tempArray[k])) {
								itmSet.add(supported.get(i) + "," + tempArray[k]);
							}

						}
					}
				}

			}
		}
		return itmSet;
	}

	public boolean checkIfUnsupported(List<String> unsupported, String temp) {
		for (int i = 0; i < unsupported.size(); i++) {
			String tempArray[] = unsupported.get(i).split(",");
			for (int j = 0; j < tempArray.length; j++) {
				if (!temp.contains(tempArray[j])) {
					return true;
				}
			}
		}
		return false;
	}

	public List<String> checkRepetition(List<String> supported) {
		boolean isRemoved = false;
		for (int i = 0; i < supported.size(); i++) {
			if (isRemoved) {
				i--;
			}
			String tempArray[] = supported.get(i).split(",");
			boolean isRepeat = true;
			for (int j = 0; j < supported.size(); j++) {
				if (i < j) {
					for (int k = 0; k < tempArray.length; k++) {
						if (!supported.get(j).contains(tempArray[k])) {
							isRepeat = false;
						}
					}
				}
			}
			if (!isRepeat) {
				supported.remove(i);
				isRemoved = true;
			} else {
				isRemoved = false;
			}

		}
		return supported;
	}

	public void generateAssosiationRules(Map<String, Float> freqMap, List<List> transaction) {
		System.out.println("The assosiation rules that are selected are as follows");
		Set<String> keys = freqMap.keySet();
		for (String s : keys) {
			String tempArray[] = s.split(",");
			if (tempArray.length < 2) {
				continue;
			} else if (tempArray.length == 2) {
				float temp1 = (freqMap.get(s) / freqMap.get(tempArray[0])) * 100;
				if (temp1 >= this.confidence) {
					System.out.print("{" + tempArray[0] + "}------>{" + tempArray[1] + "}    ");
					System.out.print("{ Support = " + freqMap.get(s) + " , Confidence = "
							+ (freqMap.get(s) / freqMap.get(tempArray[0])) * 100 + " }");
					System.out.println("  : Rule is selected");
				}
				float temp2 = (freqMap.get(s) / freqMap.get(tempArray[1])) * 100;
				if (temp2 >= this.confidence) {
					System.out.print("{" + tempArray[1] + "}------>{" + tempArray[0] + "}    ");
					System.out.print("{ Support = " + freqMap.get(s) + " , Confidence = "
							+ (freqMap.get(s) / freqMap.get(tempArray[1])) * 100 + " }");
					System.out.println("  : Rule is selected");
				}
			} else {
				int t = (tempArray.length) / 2;
				for (int i = 1; i <= (tempArray.length) / 2; i++) {
					int size = i - 1;

					for (int j = 0; j < tempArray.length; j++) {
						String temp1 = "";
						String temp2 = "";
						if (size != 0) {

							for (int k = j + 1; k < tempArray.length - size + 1; k++) {
								temp1 = "";
								temp2 = "";
								int m = k + size - 1;
								for (int z = 0; z < tempArray.length; z++) {
									if (z != j && (z < k || z > m)) {
										if (temp1.length() == 0) {
											temp1 = temp1 + tempArray[z];
										} else {
											temp1 = temp1 + "," + tempArray[z];
										}
									} else {
										if (temp2.length() == 0) {
											temp2 = temp2 + tempArray[z];
										} else {
											temp2 = temp2 + "," + tempArray[z];
										}
									}
								}
								float conf1 = (freqMap.get(s) / getSupport(temp2, transaction)) * 100;
								if (conf1 >= this.confidence) {
									System.out.print("{" + temp2 + "}----->{" + temp1 + "}");
									System.out
											.print("{ Support = " + freqMap.get(s) + " , Confidence = " + conf1 + " }");
									System.out.println("  : Rule is selected");
								}

								if (tempArray.length % 2 == 0 && i != tempArray.length / 2) {
									float conf2 = (freqMap.get(s) / getSupport(temp1, transaction)) * 100;
									if (conf2 >= this.confidence) {
										System.out.print("{" + temp1 + "}----->{" + temp2 + "}");
										System.out.print(
												"{ Support = " + freqMap.get(s) + " , Confidence = " + conf2 + " }");
										System.out.println("  : Rule is selected");
									}

								}

							}
						} else {
							for (int z = 0; z < tempArray.length; z++) {
								if (z != j) {
									if (temp1.length() == 0) {
										temp1 = temp1 + tempArray[z];
									} else {
										temp1 = temp1 + "," + tempArray[z];
									}
								} else {
									if (temp2.length() == 0) {
										temp2 = temp2 + tempArray[z];
									} else {
										temp2 = temp2 + "," + tempArray[z];
									}
								}
							}
							float conf1 = (freqMap.get(s) / getSupport(temp2, transaction)) * 100;
							if (conf1 >= this.confidence) {
								System.out.print("{" + temp2 + "}----->{" + temp1 + "}");
								System.out.print("{ Support = " + freqMap.get(s) + " , Confidence = " + conf1 + " }");
								System.out.println("  : Rule is selected");
							}

							float conf2 = (freqMap.get(s) / getSupport(temp1, transaction)) * 100;
							if (conf2 >= this.confidence) {
								System.out.print("{" + temp1 + "}----->{" + temp2 + "}");
								System.out.print("{ Support = " + freqMap.get(s) + " , Confidence = " + conf2 + " }");
								System.out.println("  : Rule is selected");
							}

						}

					}
				}

			}
		}
	}

	public float getSupport(String item, List<List> transaction) {
		String tempArray[] = item.split(",");
		float count = 0;
		for (int j = 0; j < transaction.size(); j++) {
			boolean isPresent = true;
			for (int k = 0; k < tempArray.length; k++) {
				if (!transaction.get(j).contains(tempArray[k])) {
					isPresent = false;
				}
			}
			if (isPresent) {
				count++;
			}
		}
		return count / 20 * 100;
	}

}
